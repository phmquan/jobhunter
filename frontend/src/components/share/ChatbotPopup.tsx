import React, { useState, useRef, useEffect } from 'react';
import { Button, Input, Avatar, Spin, message } from 'antd';
import { MessageOutlined, CloseOutlined, SendOutlined, RobotOutlined, UserOutlined } from '@ant-design/icons';
import axios from 'axios';
import { API_ENDPOINTS } from '@/config/api';
import * as pdfjsLib from 'pdfjs-dist';
// Vite-compatible worker import for pdfjs-dist
import workerSrc from 'pdfjs-dist/build/pdf.worker.min.mjs?url';
pdfjsLib.GlobalWorkerOptions.workerSrc = workerSrc;
import './ChatbotPopup.scss';

interface Message {
  id: string;
  text: string;
  sender: 'user' | 'bot';
  timestamp: Date;
}

interface ChatbotPopupProps {
  isVisible: boolean;
  onToggle: () => void;
}

const fetchPdfAsBlob = async (pdfUrl: string): Promise<Blob> => {
  const response = await fetch(pdfUrl, { mode: 'cors' });
  if (!response.ok) throw new Error('Failed to fetch PDF');
  return await response.blob();
};

const extractTextFromPdfBlob = async (pdfBlob: Blob): Promise<string> => {
  const arrayBuffer = await pdfBlob.arrayBuffer();
  const loadingTask = pdfjsLib.getDocument({ data: arrayBuffer });
  const pdf = await loadingTask.promise;
  let text = '';
  for (let i = 1; i <= pdf.numPages; i++) {
    const page = await pdf.getPage(i);
    const content = await page.getTextContent();
    text += content.items.map((item: any) => item.str).join(' ') + '\n';
  }
  return text;
};

const ChatbotPopup: React.FC<ChatbotPopupProps> = ({ isVisible, onToggle }) => {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '1',
      text: 'Hello! I\'m your career coach assistant. I can help you analyze job requirements and provide personalized advice for your career development. How can I help you today?',
      sender: 'bot',
      timestamp: new Date()
    }
  ]);
  const [inputText, setInputText] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [cvText, setCvText] = useState<string>('');
  const [cvLoading, setCvLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<any>(null);

  // Fetch user's resume (PDF) and extract text when chatbot opens
  useEffect(() => {
    const fetchAndExtractCV = async () => {
      setCvLoading(true);
      try {
        const token = localStorage.getItem('access_token');
        console.log('JWT token before fetch:', token);
        const res = await axios.get('http://localhost:8080/api/v1/resumes/by-user/latest', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        console.log('Resume response:', res);
        const resume = res?.data.data;
        if (resume && resume.url) {
          const pdfUrl = resume.url.startsWith('resume/')
            ? `http://localhost:8080/api/v1/files/${resume.url}`
            : `http://localhost:8080/api/v1/files/${resume.url}`;
          console.log('PDF URL:', pdfUrl);
          const pdfBlob = await fetchPdfAsBlob(pdfUrl);
          const text = await extractTextFromPdfBlob(pdfBlob);
          setCvText(text);
        } else {
          setCvText('');
        }
      } catch (err) {
        console.error('Error fetching resume:', err);
        setCvText('');
      } finally {
        setCvLoading(false);
      }
    };
    if (isVisible) {
      fetchAndExtractCV();
    }
  }, [isVisible]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  useEffect(() => {
    if (isVisible && inputRef.current) {
      setTimeout(() => {
        inputRef.current?.focus();
      }, 100);
    }
  }, [isVisible]);

  const handleSendMessage = async () => {
    if (!inputText.trim() || isLoading) return;

    const userMessage: Message = {
      id: Date.now().toString(),
      text: inputText.trim(),
      sender: 'user',
      timestamp: new Date()
    };

    setMessages(prev => [...prev, userMessage]);
    setInputText('');
    setIsLoading(true);

    try {
      // Prepend CV text to the prompt
      const prompt = `User CV (extracted from PDF):\n${cvText}\n\nUser question: ${userMessage.text}`;
      const response = await axios.post(API_ENDPOINTS.GENERATE_RESPONSE, {
        query_text: prompt,
        top_k: 5
      });

      const botMessage: Message = {
        id: (Date.now() + 1).toString(),
        text: response.data.response,
        sender: 'bot',
        timestamp: new Date()
      };

      setMessages(prev => [...prev, botMessage]);
    } catch (error) {
      console.error('Error sending message:', error);
      message.error('Sorry, I encountered an error. Please try again.');
      
      const errorMessage: Message = {
        id: (Date.now() + 1).toString(),
        text: 'Sorry, I encountered an error. Please try again.',
        sender: 'bot',
        timestamp: new Date()
      };

      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  const formatTime = (date: Date) => {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  if (!isVisible) {
    return (
      <div className="chatbot-toggle">
        <Button
          type="primary"
          shape="circle"
          size="large"
          icon={<MessageOutlined />}
          onClick={onToggle}
          className="chatbot-toggle-button"
        />
      </div>
    );
  }

  return (
    <div className="chatbot-popup">
      <div className="chatbot-header">
        <div className="chatbot-title">
          <RobotOutlined className="chatbot-icon" />
          <span>Career Coach Assistant</span>
        </div>
        <Button
          type="text"
          icon={<CloseOutlined />}
          onClick={onToggle}
          className="chatbot-close"
        />
      </div>

      <div className="chatbot-messages">
        {cvLoading && (
          <div className="message bot-message">
            <div className="message-content">
              <div className="message-avatar">
                <Avatar icon={<RobotOutlined />} size="small" />
              </div>
              <div className="message-bubble">
                <div className="message-text">
                  <Spin size="small" /> Loading your CV...
                </div>
              </div>
            </div>
          </div>
        )}
        {messages.map((message) => (
          <div
            key={message.id}
            className={`message ${message.sender === 'user' ? 'user-message' : 'bot-message'}`}
          >
            <div className="message-content">
              <div className="message-avatar">
                {message.sender === 'user' ? (
                  <Avatar icon={<UserOutlined />} size="small" />
                ) : (
                  <Avatar icon={<RobotOutlined />} size="small" />
                )}
              </div>
              <div className="message-bubble">
                <div className="message-text">{message.text}</div>
                <div className="message-time">{formatTime(message.timestamp)}</div>
              </div>
            </div>
          </div>
        ))}
        
        {isLoading && (
          <div className="message bot-message">
            <div className="message-content">
              <div className="message-avatar">
                <Avatar icon={<RobotOutlined />} size="small" />
              </div>
              <div className="message-bubble">
                <div className="message-text">
                  <Spin size="small" /> Thinking...
                </div>
              </div>
            </div>
          </div>
        )}
        
        <div ref={messagesEndRef} />
      </div>

      <div className="chatbot-input">
        <Input.TextArea
          ref={inputRef}
          value={inputText}
          onChange={(e) => setInputText(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder="Type your message here..."
          autoSize={{ minRows: 1, maxRows: 4 }}
          disabled={isLoading}
          className="chatbot-textarea"
        />
        <Button
          type="primary"
          icon={<SendOutlined />}
          onClick={handleSendMessage}
          disabled={!inputText.trim() || isLoading}
          className="chatbot-send-button"
        />
      </div>
    </div>
  );
};

export default ChatbotPopup; 