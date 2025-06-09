# Chatbot Feature

## Overview
The chatbot popup is a career coach assistant that helps users analyze job requirements and provides personalized advice for career development. It integrates with the AI Python backend to provide intelligent responses based on job data.

## Features
- **Floating Chat Button**: A circular button in the bottom-right corner of the screen
- **Modern UI**: Clean, responsive design with smooth animations
- **Real-time Chat**: Instant messaging interface with typing indicators
- **AI Integration**: Connects to FastAPI backend for intelligent responses
- **Mobile Responsive**: Works on both desktop and mobile devices
- **Dark Mode Support**: Automatically adapts to system theme preferences

## How to Use

### Starting the Backend
1. Navigate to the `AIPython` directory
2. Install dependencies: `pip install -r requirements.txt`
3. Start the FastAPI server: `uvicorn app:app --reload --port 8000`

### Starting the Frontend
1. Navigate to the `frontend` directory
2. Install dependencies: `npm install`
3. Start the development server: `npm run dev`

### Using the Chatbot
1. The chatbot button appears as a floating circular button in the bottom-right corner
2. Click the button to open the chat interface
3. Type your questions about job requirements, career advice, or skill analysis
4. The AI will respond with personalized advice based on the job data in the system
5. Click the close button (X) to minimize the chat

## API Endpoints
- `POST /generate-response/`: Generates AI responses based on user queries
- `POST /embed-job-requirement/`: Embeds job requirements into the vector database

## Configuration
The chatbot connects to the AI backend at `http://localhost:8000`. You can modify the API base URL in `src/config/api.ts` if needed.

## Styling
The chatbot uses SCSS for styling with:
- Modern gradient backgrounds
- Smooth animations and transitions
- Responsive design for mobile devices
- Custom scrollbars
- Hover effects and visual feedback

## Components
- `ChatbotPopup.tsx`: Main chatbot component
- `ChatbotPopup.scss`: Styling for the chatbot
- Integrated into `App.tsx` for global availability

## Error Handling
- Network errors are caught and displayed to the user
- Loading states prevent multiple simultaneous requests
- Graceful fallbacks for failed API calls 