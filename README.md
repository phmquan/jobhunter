# JobHunter
JobHunter is a job hunting assistant application powered by LLM-based chat bot to provide job advice, resume review, and job requirement analysis.
This project consists of:

Spring Boot (Java): Backend service for user management, job tracking, and system integration.

React + TypeScript: Frontend user interface.

FastAPI (Python): Backend service for AI chat and RAG (Retrieval Augmented Generation) based job requirement search.

MySQL: Main database.

Local Vector Database: Used for similarity search and RAG functionality.

Ollama + fine-tuned LLM model: Powering the AI chat advice.

🔄 Chat Flow


⚙️ Installation
1️⃣ Clone the repository
bash
Copy
Edit
git clone https://github.com/your-username/jobhunter.git
cd jobhunter
2️⃣ Setup Backend (Spring Boot)
bash
Copy
Edit
cd backend
Open src/main/resources/application.properties

Update the following configuration:

MySQL database URL, username, password

Redis connection

Email server settings

Run the Spring Boot backend
bash
Copy
Edit
./mvnw spring-boot:run
3️⃣ Setup Frontend (React + TypeScript)
bash
Copy
Edit
cd frontend
Install dependencies:

bash
Copy
Edit
npm install
Build the frontend:

bash
Copy
Edit
npm run build
Run frontend (choose one depending on your needs):

bash
Copy
Edit
npm run preview    # For production preview
npm run dev        # For development mode
4️⃣ Setup LLM Chatbot Backend (FastAPI)
Install Ollama
Install Ollama from: https://ollama.com/download

Start your fine-tuned model:

bash
Copy
Edit
ollama run https://huggingface.co/hoanghuy100202/FineTune_JobAdviceApplication
Setup FastAPI server
bash
Copy
Edit
cd AiPython
pip install -r requirements.txt
uvicorn app:app --reload --port 8000
📦 Project Structure
bash
Copy
Edit
jobhunter/
│
├── backend/           # Spring Boot backend
├── frontend/          # React + TypeScript frontend
├── AiPython/          # FastAPI + LLM backend
├── README.md
└── ...
🛠️ Tech Stack
Java Spring Boot

React.js (TypeScript)

FastAPI (Python)

MySQL

Redis

Ollama LLM with Fine-tune model

Local Vector DB (for RAG)

💡 Features
📝 LLM-powered job advice chat bot

📄 Resume analysis

🔍 Job requirement extraction with RAG search

📊 Job application tracking

🔐 Secure login and account management

📬 Contribution
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

📄 License
This project is licensed under the MIT License - see the LICENSE file for details.
