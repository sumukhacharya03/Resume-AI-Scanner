import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JobMatcher {
    
    // Extract keywords from job description
    public List<String> extractJobKeywords(String jobDescription) {
        List<String> keywords = new ArrayList<>();
        ResumeParser parser = new ResumeParser();
        
        // Extract the same type of skills from job description as we do from resume
        keywords.addAll(parser.extractSkills(jobDescription));
        
        // Additional job-specific requirements
        Pattern requirementPattern = Pattern.compile("(?i)(required|requirement|must have|essential|necessary|qualification)s?:?([^.;]*)[.;]");
        Matcher matcher = requirementPattern.matcher(jobDescription);
        while (matcher.find() && matcher.group(2) != null) {
            String requirement = matcher.group(2).trim();
            if (!requirement.isEmpty()) {
                keywords.add(requirement);
            }
        }
        
        return keywords;
    }
    
    public MatchResult calculateMatchScore(List<String> resumeSkills, List<String> resumeEducation, 
                                     List<String> resumeExperience, String jobDescription) {
        List<String> jobKeywords = extractJobKeywords(jobDescription);
        
        // Calculate job match score with TF-IDF inspired weighting
        Map<String, Double> keywordWeights = calculateKeywordWeights(jobDescription, jobKeywords);
        
        double totalWeight = keywordWeights.values().stream().mapToDouble(Double::doubleValue).sum();
        // Add extra weight for education and experience components
        double maxPossibleWeight = totalWeight + 
                              (resumeEducation.size() * 0.5) + 
                              (resumeExperience.size() * 0.75);
        
        double matchedWeight = 0.0;
        List<String> matchedKeywords = new ArrayList<>();
        
        // Improve matching for partial matches and handle case sensitivity
        // Check skills matches (highest weight)
        for (String skill : resumeSkills) {
            for (Map.Entry<String, Double> entry : keywordWeights.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(skill) || 
                    containsIgnoreCase(entry.getKey(), skill) ||
                    containsIgnoreCase(skill, entry.getKey()) ||
                    jobDescription.toLowerCase().contains(skill.toLowerCase())) {
                    matchedWeight += entry.getValue();
                    matchedKeywords.add(skill);
                    break;
                }
            }
        }
        
        // Check education and experience matches (lower weight)
        for (String edu : resumeEducation) {
            if (containsIgnoreCase(jobDescription, edu)) {
                matchedWeight += 0.5; // Lower weight for education match
                matchedKeywords.add(edu);
            }
        }
        
        for (String exp : resumeExperience) {
            if (containsIgnoreCase(jobDescription, exp)) {
                matchedWeight += 0.75; // Medium weight for experience match
                matchedKeywords.add(exp);
            }
            
            // Check for relevant skills in experience
            for (Map.Entry<String, Double> entry : keywordWeights.entrySet()) {
                if (containsIgnoreCase(exp, entry.getKey())) {
                    matchedWeight += 0.5 * entry.getValue(); // Half weight when found in experience
                    matchedKeywords.add(entry.getKey() + " (in experience)");
                    break;
                }
            }
        }
        
        // Calculate match score as percentage of maximum possible weight
        // Cap at 100 to ensure score is between 0-100
        double matchScore = (maxPossibleWeight > 0) ? Math.min(100, (matchedWeight / maxPossibleWeight) * 100) : 0;
        
        // Find missing important keywords
        List<String> missingKeywords = new ArrayList<>();
        for (Map.Entry<String, Double> entry : keywordWeights.entrySet()) {
            if (entry.getValue() > 0.5) { // Only consider important keywords (higher weight)
                boolean found = false;
                for (String skill : resumeSkills) {
                    if (entry.getKey().equalsIgnoreCase(skill) || 
                        containsIgnoreCase(skill, entry.getKey()) ||
                        containsIgnoreCase(entry.getKey(), skill)) {
                        found = true;
                        break;
                    }
                }
                
                // Also check if skill appears in experience
                if (!found) {
                    for (String exp : resumeExperience) {
                        if (containsIgnoreCase(exp, entry.getKey())) {
                            found = true;
                            break;
                        }
                    }
                }
                
                if (!found) {
                    missingKeywords.add(entry.getKey());
                }
            }
        }
        
        return new MatchResult(matchScore, matchedKeywords, missingKeywords);
    }

    // Helper method to check if one string contains another, ignoring case
    private boolean containsIgnoreCase(String source, String target) {
        if (source == null || target == null) return false;
        return source.toLowerCase().contains(target.toLowerCase());
    }
    
    private Map<String, Double> calculateKeywordWeights(String jobDescription, List<String> keywords) {
        Map<String, Double> weights = new HashMap<>();
        
        // Simplified TF-IDF approach
        for (String keyword : keywords) {
            // Calculate term frequency
            String lowerCaseKeyword = keyword.toLowerCase();
            String lowerCaseJobDescription = jobDescription.toLowerCase();
            
            // Check for exact match
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(lowerCaseKeyword) + "\\b");
            Matcher matcher = pattern.matcher(lowerCaseJobDescription);
            
            int count = 0;
            while (matcher.find()) {
                count++;
            }
            
            // Calculate weight based on frequency and position in document
            double weight = count * 0.5;
            
            // Increase weight if keyword appears in requirements/qualifications section
            if (lowerCaseJobDescription.contains("required " + lowerCaseKeyword) ||
                lowerCaseJobDescription.contains("qualification " + lowerCaseKeyword) ||
                lowerCaseJobDescription.contains("must have " + lowerCaseKeyword)) {
                weight *= 2.0;
            }
            
            // Increase weight if it appears in title or first paragraph
            int firstParagraphEnd = lowerCaseJobDescription.indexOf("\n\n");
            if (firstParagraphEnd == -1) firstParagraphEnd = lowerCaseJobDescription.length();
            String firstParagraph = lowerCaseJobDescription.substring(0, firstParagraphEnd);
            
            if (firstParagraph.contains(lowerCaseKeyword)) {
                weight *= 1.5;
            }
            
            weights.put(keyword, weight);
        }
        
        return weights;
    }
    
    // Class to hold match results
    public static class MatchResult {
        private double score;
        private List<String> matchedKeywords;
        private List<String> missingKeywords;
        
        public MatchResult(double score, List<String> matchedKeywords, List<String> missingKeywords) {
            this.score = score;
            this.matchedKeywords = matchedKeywords;
            this.missingKeywords = missingKeywords;
        }
        
        public double getScore() {
            return score;
        }
        
        public List<String> getMatchedKeywords() {
            return matchedKeywords;
        }
        
        public List<String> getMissingKeywords() {
            return missingKeywords;
        }
    }
    
    // Compare resume with job description file
    public MatchResult compareWithJob(String resumeText, String jobDescriptionFile) throws IOException {
        String jobDescription = new String(Files.readAllBytes(Paths.get(jobDescriptionFile)));
        
        ResumeParser parser = new ResumeParser();
        List<String> resumeSkills = parser.extractSkills(resumeText);
        List<String> resumeEducation = parser.extractEducation(resumeText);
        List<String> resumeExperience = parser.extractExperience(resumeText);
        
        return calculateMatchScore(resumeSkills, resumeEducation, resumeExperience, jobDescription);
    }
}
