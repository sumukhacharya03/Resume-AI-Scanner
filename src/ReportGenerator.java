import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportGenerator {
    public void generateReport(String resumeFile, JobMatcher.MatchResult matchResult, String suggestions) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String reportFile = "data/report_" + timestamp + ".txt";
        
        FileWriter writer = new FileWriter(reportFile);
        
        // Add header with timestamp
        writer.write("=======================================================\n");
        writer.write("                ATS COMPATIBILITY REPORT               \n");
        writer.write("=======================================================\n\n");
        writer.write("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
        writer.write("Resume File: " + resumeFile + "\n\n");
        
        // Format score with 2 decimal places and add visual representation
        double score = matchResult.getScore();
        writer.write("JOB MATCH SCORE: " + String.format("%.2f", score) + "%\n");
        
        // Visual representation of score
        writer.write("[");
        int filledBlocks = (int) (score / 5); // Each block represents 5%
        for (int i = 0; i < 20; i++) {
            if (i < filledBlocks) {
                writer.write("█");
            } else {
                writer.write("░");
            }
        }
        writer.write("]\n\n");
        
        // Score interpretation
        writer.write("Score Interpretation:\n");
        if (score >= 80) {
            writer.write("Excellent match! Your resume is well-aligned with the job requirements.\n");
        } else if (score >= 60) {
            writer.write("Good match. With a few improvements, your resume could be more competitive.\n");
        } else if (score >= 40) {
            writer.write("Average match. Consider implementing the suggested improvements to increase your chances.\n");
        } else {
            writer.write("Below average match. Significant improvements may be needed to align with this job.\n");
        }
        writer.write("\n");
        
        // Matched keywords
        List<String> matchedKeywords = matchResult.getMatchedKeywords();
        writer.write("MATCHED KEYWORDS (" + matchedKeywords.size() + "):\n");
        if (!matchedKeywords.isEmpty()) {
            for (String keyword : matchedKeywords) {
                writer.write("✓ " + keyword + "\n");
            }
        } else {
            writer.write("No keywords matched.\n");
        }
        writer.write("\n");
        
        // Add suggestions section
        writer.write("=======================================================\n");
        writer.write(suggestions);
        writer.write("\n=======================================================\n");
        writer.write("NEXT STEPS:\n");
        writer.write("1. Update your resume based on the suggestions above\n");
        writer.write("2. Run the analysis again to verify improvements\n");
        writer.write("3. Tailor your resume for each specific job application\n");
        
        writer.close();
        System.out.println("Detailed Report Generated: " + reportFile);
    }
}
