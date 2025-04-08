import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // Default file paths - can be made configurable via command-line arguments
        String resumeFile = "data/sample_resume.pdf";
        String jobDescriptionFile = "data/job_description.txt";
        
        System.out.println("Starting resume analysis...");
        System.out.println("Resume: " + resumeFile);
        System.out.println("Job Description: " + jobDescriptionFile);
        
        // Parse resume
        ResumeParser parser = new ResumeParser();
        String resumeText = parser.extractText(resumeFile);
        
        if (resumeText.equals("Error extracting text")) {
            System.err.println("Error: Could not extract text from resume. Please check the file path and format.");
            return;
        }
        
        // Extract resume components
        System.out.println("\nExtracting skills from resume...");
        System.out.println("Found skills: " + parser.extractSkills(resumeText));
        System.out.println("Found education: " + parser.extractEducation(resumeText));
        System.out.println("Found experience: " + parser.extractExperience(resumeText));
        
        // Match with job description
        System.out.println("\nComparing with job description...");
        JobMatcher matcher = new JobMatcher();
        JobMatcher.MatchResult matchResult = matcher.compareWithJob(resumeText, jobDescriptionFile);
        
        System.out.println("Match score: " + String.format("%.2f", matchResult.getScore()) + "%");
        System.out.println("Matched keywords: " + matchResult.getMatchedKeywords());
        System.out.println("Missing important keywords: " + matchResult.getMissingKeywords());
        
        // Generate suggestions
        System.out.println("\nGenerating optimization suggestions...");
        ATSOptimizer optimizer = new ATSOptimizer();
        String jobDescription = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(jobDescriptionFile)));
        String suggestions = optimizer.suggestEnhancements(resumeText, jobDescription, matchResult.getMissingKeywords());
        
        // Generate report
        System.out.println("\nGenerating comprehensive report...");
        ReportGenerator reportGen = new ReportGenerator();
        reportGen.generateReport(resumeFile, matchResult, suggestions);
    }
}
