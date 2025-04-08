import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ATSOptimizer {
    
    public String suggestEnhancements(String resumeText, String jobDescription, List<String> missingKeywords) {
        StringBuilder suggestions = new StringBuilder("Suggested Improvements:\n\n");
        
        // Suggest missing keywords
        if (!missingKeywords.isEmpty()) {
            suggestions.append("1. Add Missing Keywords:\n");
            for (String keyword : missingKeywords) {
                suggestions.append("   - ").append(keyword).append("\n");
            }
            suggestions.append("\n");
        }
        
        // Analyze resume format
        Map<String, String> formatSuggestions = analyzeResumeFormat(resumeText);
        if (!formatSuggestions.isEmpty()) {
            suggestions.append("2. Formatting Improvements:\n");
            for (Map.Entry<String, String> entry : formatSuggestions.entrySet()) {
                suggestions.append("   - ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            suggestions.append("\n");
        }
        
        // Content optimization suggestions
        suggestions.append("3. Content Optimization:\n");
        
        // Check for action verbs
        if (!containsActionVerbs(resumeText)) {
            suggestions.append("   - Use more action verbs (like 'achieved', 'implemented', 'developed')\n");
        }
        
        // Check for quantifiable achievements
        if (!containsQuantifiableAchievements(resumeText)) {
            suggestions.append("   - Add quantifiable achievements (percentages, numbers, metrics)\n");
        }
        
        // Keyword frequency analysis
        suggestions.append("   - Ensure proper keyword density (avoid overuse or underuse)\n");
        
        // Check if contact information is prominent
        if (!hasProminentContactInfo(resumeText)) {
            suggestions.append("   - Make contact information more prominent\n");
        }
        
        return suggestions.toString();
    }
    
    private Map<String, String> analyzeResumeFormat(String resumeText) {
        Map<String, String> suggestions = new HashMap<>();
        
        // Check for common formatting issues
        if (resumeText.contains("Objective:") || resumeText.contains("Career Objective:")) {
            suggestions.put("Objective Statement", "Consider replacing with a professional summary that highlights relevant skills");
        }
        
        if (resumeText.length() > 5000) { // Rough check for resume length
            suggestions.put("Length", "Resume appears lengthy. Consider condensing to 1-2 pages");
        }
        
        // Check for references section
        if (resumeText.contains("References:") || resumeText.contains("References available upon request")) {
            suggestions.put("References", "Remove references section to save space");
        }
        
        // Check for consistent formatting (rough heuristic)
        if (countOccurrences(resumeText, "\n\n") > 20) {
            suggestions.put("Spacing", "Check for inconsistent spacing");
        }
        
        return suggestions;
    }
    
    private boolean containsActionVerbs(String resumeText) {
        String[] actionVerbs = {"achieved", "improved", "increased", "reduced", "managed", "led", "developed", 
                              "created", "implemented", "designed", "coordinated", "conducted", "analyzed"};
        
        for (String verb : actionVerbs) {
            if (resumeText.contains(verb) || resumeText.contains(verb.substring(0, 1).toUpperCase() + verb.substring(1))) {
                return true;
            }
        }
        return false;
    }
    
    private boolean containsQuantifiableAchievements(String resumeText) {
        // Check for numbers followed by % or common metrics
        return resumeText.matches(".*(\\d+)\\s*%.*") || 
               resumeText.matches(".*(\\d+)\\s*(users|customers|clients|projects|dollars|revenue).*");
    }
    
    private boolean hasProminentContactInfo(String resumeText) {
        // Check if contact info appears at beginning of resume
        String firstFewLines = resumeText.substring(0, Math.min(resumeText.length(), 500));
        return firstFewLines.matches(".*\\b([A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,})\\b.*") || // Email
               firstFewLines.matches(".*\\b(\\d{3}[-.]?\\d{3}[-.]?\\d{4})\\b.*"); // Phone
    }
    
    private int countOccurrences(String text, String substring) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        return count;
    }
}
