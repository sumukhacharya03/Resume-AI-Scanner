# Resume AI Scanner

An intelligent resume analysis tool that evaluates how well your resume matches a job description using AI-powered techniques.

## About the Project

This project helps job seekers optimize their resumes for Applicant Tracking Systems (ATS) by:
- Analyzing resume content against job descriptions
- Identifying matching and missing keywords
- Providing actionable improvement suggestions
- Generating detailed compatibility reports

**Key Features:**
- PDF resume text extraction
- Skill, education, and experience parsing
- Job description keyword analysis
- Match score calculation
- ATS optimization recommendations

## How AI is Influencing ATS Resume Scanning

Modern ATS systems increasingly incorporate AI to:
1. **Semantic Analysis** - Understand context beyond exact keyword matching
2. **Skill Inference** - Identify transferable skills from experience descriptions
3. **Bias Reduction** - Focus on qualifications rather than demographic clues
4. **Predictive Scoring** - Estimate candidate suitability using ML models

Our implementation demonstrates these concepts through:
- TF-IDF inspired keyword weighting
- Contextual requirement extraction
- Partial/fuzzy matching algorithms
- Comprehensive gap analysis

## How to Run

### Prerequisites
- Java 11 or higher
- Apache PDFBox 3.0.4

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/Resume-AI-Scanner.git
   cd Resume-AI-Scanner
2. Prepare sample files:
   - Place your resume PDF in data/sample_resume.pdf
   - Place job description in data/job_description.txt
### Execution
1. Compile the project:
   ```bash
   javac -cp ".:./libs/pdfbox-app-3.0.4.jar" src/*.java -d bin/
2. Run the application:
   ```bash
   java -cp "bin:./libs/pdfbox-app-3.0.4.jar" ResumeAnalyzerUI
3. Use the graphical interface to:

- Select your resume and job description
- View analysis results
- Generate optimization report

## Future Work

1. **Better Matching** - Add smarter AI to understand similar skills (e.g., "Python" ↔ "Django")  
2. **More File Types** - Support DOCX and LinkedIn profile imports  
3. **Live Editing** - Build a real-time resume editor with instant scoring  
4. **Job Suggestions** - Recommend suitable jobs based on resume content  
5. **Mobile App** - Create an Android/iOS version for on-the-go optimization  
