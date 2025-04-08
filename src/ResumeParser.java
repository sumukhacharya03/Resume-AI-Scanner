import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class ResumeParser {

    // Extract text from the resume PDF
    public String extractText(String filePath) {
        try (PDDocument document = Loader.loadPDF(new File(filePath))) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error extracting text";
        }
    }

    // Extract skills dynamically from resume text
    public List<String> extractSkills(String resumeText) {
        List<String> skills = new ArrayList<>();
        
        // Common technical skill patterns
        Pattern techPattern = Pattern.compile("\\b(Java|Python|C\\+\\+|JavaScript|React|Angular|Vue|Node\\.js|Express|Spring|Hibernate|SQL|NoSQL|MongoDB|MySQL|PostgreSQL|AWS|Azure|GCP|Docker|Kubernetes|CI/CD|Jenkins|Git|REST|SOAP|Microservices|Machine Learning|AI|Data Science|TensorFlow|PyTorch|NLP|Computer Vision|Agile|Scrum|DevOps|HTML|CSS|XML|JSON)\\b");
        
        // Software/frameworks pattern
        Pattern frameworkPattern = Pattern.compile("\\b(Spring Boot|Django|Flask|Laravel|ASP\\.NET|Ruby on Rails|Symfony|Bootstrap|JUnit|Mockito|Selenium|Cypress|Jest|Mocha|Chai)\\b");
        
        // Concepts pattern
        Pattern conceptPattern = Pattern.compile("\\b(Object-Oriented Programming|OOP|Functional Programming|Data Structures|Algorithms|Design Patterns|MVC|MVVM|REST API|GraphQL|SOLID|Clean Code|TDD|BDD|Multithreading|Concurrency|Parallel Computing|Distributed Systems|Cloud Computing|Serverless|Blockchain)\\b");
        
        // Extract skills using patterns
        addMatchesToList(techPattern.matcher(resumeText), skills);
        addMatchesToList(frameworkPattern.matcher(resumeText), skills);
        addMatchesToList(conceptPattern.matcher(resumeText), skills);
        
        return skills;
    }
    
    private void addMatchesToList(Matcher matcher, List<String> list) {
        Set<String> uniqueMatches = new HashSet<>(); // To avoid duplicates
        while (matcher.find()) {
            uniqueMatches.add(matcher.group());
        }
        list.addAll(uniqueMatches);
    }
    
    // Extract education information
    public List<String> extractEducation(String resumeText) {
        List<String> education = new ArrayList<>();
        
        // Define the education section pattern
        Pattern eduSectionPattern = Pattern.compile("(?i)\\bEDUCATION\\b[\\s\\S]*?(?=\\b(WORK|EXPERIENCE|SKILLS|PROJECTS|HONORS|AWARDS|ACTIVITIES|RESEARCH)\\b|$)");
        Matcher sectionMatcher = eduSectionPattern.matcher(resumeText);
        
        if (sectionMatcher.find()) {
            String educationSection = sectionMatcher.group().trim();
            
            // First, try to extract specific degree information
            Pattern degreePattern = Pattern.compile("(?i)(Bachelor|Master|PhD|B\\.Tech|M\\.Tech|MBA|B\\.S\\.|M\\.S\\.|B\\.A\\.|M\\.A\\.|B\\.E\\.).{0,20}?(of|in)\\s.{0,100}?(Engineering|Science|Computer Science|Information Technology|Technology)");
            Matcher degreeMatcher = degreePattern.matcher(educationSection);
            
            if (degreeMatcher.find()) {
                String degree = degreeMatcher.group().trim();
                // Remove any line breaks within the degree
                degree = degree.replaceAll("\\s+", " ").trim();
                education.add(degree);
                return education;
            }
            
            // If no specific degree found, take the most relevant line
            String[] lines = educationSection.split("\n");
            for (int i = 1; i < lines.length; i++) { // Start from 1 to skip the "EDUCATION" header
                String line = lines[i].trim();
                if (!line.isEmpty() && 
                    (line.contains("Bachelor") || 
                     line.contains("Master") || 
                     line.contains("B.Tech") || 
                     line.contains("Computer Science") || 
                     line.contains("Engineering"))) {
                    education.add(line);
                    return education;
                }
            }
            
            // If still no match, take the first non-empty line after the header
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (!line.isEmpty() && !line.equalsIgnoreCase("EDUCATION")) {
                    education.add(line);
                    return education;
                }
            }
        }
        
        // If all else fails, search for degree information anywhere in the text
        if (education.isEmpty()) {
            Pattern generalDegreePattern = Pattern.compile("(?i)(Bachelor|Master|PhD|B\\.Tech|M\\.Tech|MBA).{0,100}?(Computer Science|Engineering|Technology)");
            Matcher generalMatcher = generalDegreePattern.matcher(resumeText);
            if (generalMatcher.find()) {
                education.add(generalMatcher.group().trim());
            }
        }
        
        return education;
    }
    
    // Extract experience information
    public List<String> extractExperience(String resumeText) {
        List<String> experience = new ArrayList<>();
        
        // Define patterns to find the experience section
        List<Pattern> sectionPatterns = new ArrayList<>();
        sectionPatterns.add(Pattern.compile("(?i)\\bWORK\\s*EXPERIENCE\\b[\\s\\S]*?(?=\\b(EDUCATION|SKILLS|PROJECTS|HONORS|AWARDS|ACTIVITIES|RESEARCH)\\b|$)"));
        sectionPatterns.add(Pattern.compile("(?i)\\bEXPERIENCE\\b[\\s\\S]*?(?=\\b(EDUCATION|SKILLS|PROJECTS|HONORS|AWARDS|ACTIVITIES|RESEARCH)\\b|$)"));
        
        String experienceSection = null;
        
        // Try to extract experience section using different patterns
        for (Pattern pattern : sectionPatterns) {
            Matcher matcher = pattern.matcher(resumeText);
            if (matcher.find()) {
                experienceSection = matcher.group().trim();
                break;
            }
        }
        
        if (experienceSection != null) {
            // Extract job title and company information
            Pattern jobPattern = Pattern.compile("(?i)([A-Za-z0-9\\s&,]+)(,|-)\\s*([A-Za-z\\s,]+)\\s*([A-Za-z0-9\\s]+\\d{4}.+?\\d{4}|[A-Za-z0-9\\s]+\\d{4}.+?Present|[A-Za-z0-9\\s]+\\d{4}.+?Current)");
            Matcher jobMatcher = jobPattern.matcher(experienceSection);
            
            if (jobMatcher.find() && jobMatcher.groupCount() >= 3) {
                String company = jobMatcher.group(1).trim();
                String location = jobMatcher.group(3).trim();
                
                // Look for the job title near this match
                int matchPosition = jobMatcher.start();
                String beforeMatch = experienceSection.substring(0, matchPosition);
                String afterMatch = experienceSection.substring(matchPosition);
                
                // Try to find job title
                Pattern titlePattern = Pattern.compile("(?i)(Intern|Software Developer|Developer|Engineer|Software Engineer|Data Scientist|Analyst|Manager)");
                
                // First look after the match (more common in some formats)
                Matcher titleMatcherAfter = titlePattern.matcher(afterMatch);
                if (titleMatcherAfter.find()) {
                    String title = titleMatcherAfter.group().trim();
                    experience.add(title + " at " + company);
                    return experience;
                }
                
                // Then look before the match
                Matcher titleMatcherBefore = titlePattern.matcher(beforeMatch);
                String title = "Professional";
                if (titleMatcherBefore.find()) {
                    // Get the last match
                    String lastMatch = null;
                    do {
                        lastMatch = titleMatcherBefore.group();
                    } while (titleMatcherBefore.find());
                    
                    if (lastMatch != null) {
                        title = lastMatch.trim();
                    }
                }
                
                experience.add(title + " at " + company);
                return experience;
            }
            
            // If we couldn't extract using the pattern, try to find intern/job title and company separately
            String[] lines = experienceSection.split("\n");
            for (int i = 1; i < lines.length; i++) { // Skip the header line
                String line = lines[i].trim();
                
                // Check if this line contains a job title
                if (line.contains("Intern") || 
                    line.contains("Developer") || 
                    line.contains("Engineer") || 
                    line.contains("Software")) {
                    
                    // If yes, look for a company name in nearby lines
                    String company = "";
                    for (int j = Math.max(1, i-1); j <= Math.min(i+1, lines.length-1); j++) {
                        if (j != i && !lines[j].trim().isEmpty()) {
                            company = lines[j].trim();
                            if (company.contains(",")) {
                                company = company.substring(0, company.indexOf(",")).trim();
                            }
                            break;
                        }
                    }
                    
                    if (!company.isEmpty()) {
                        experience.add(line + " at " + company);
                    } else {
                        experience.add(line);
                    }
                    return experience;
                }
            }
            
            // Last resort: take the first two non-empty lines after the header
            String title = "";
            String company = "";
            int nonEmptyCount = 0;
            
            for (int i = 1; i < lines.length && nonEmptyCount < 2; i++) {
                String line = lines[i].trim();
                if (!line.isEmpty()) {
                    if (nonEmptyCount == 0) {
                        company = line;
                    } else {
                        title = line;
                    }
                    nonEmptyCount++;
                }
            }
            
            if (!company.isEmpty()) {
                if (title.isEmpty()) {
                    // If we only found one line, assume it's the company
                    experience.add("Professional at " + company);
                } else {
                    experience.add(title + " at " + company);
                }
                return experience;
            }
        }
        
        // If nothing found, search for common job titles anywhere in the text
        if (experience.isEmpty()) {
            Pattern titlePattern = Pattern.compile("(?i)(Software Development Intern|Intern|Software Developer|Developer|Engineer|Software Engineer)");
            Matcher titleMatcher = titlePattern.matcher(resumeText);
            if (titleMatcher.find()) {
                experience.add(titleMatcher.group().trim());
            }
        }
        
        return experience;
    }
}
