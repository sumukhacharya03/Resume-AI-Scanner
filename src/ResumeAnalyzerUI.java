import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResumeAnalyzerUI extends JFrame {
    private JTextField resumePathField;
    private JTextField jobDescPathField;
    private JTextArea resultArea;
    private JButton analyzeButton;
    private JButton browseResumeButton;
    private JButton browseJobButton;

    public ResumeAnalyzerUI() {
        setTitle("Resume Analyzer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        resumePathField = new JTextField(30);
        jobDescPathField = new JTextField(30);
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        analyzeButton = new JButton("Analyze Resume");
        browseResumeButton = new JButton("Browse...");
        browseJobButton = new JButton("Browse...");

        // Set default paths
        resumePathField.setText("data/sample_resume.pdf");
        jobDescPathField.setText("data/job_description.txt");

        // Add action listeners
        analyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analyzeResume();
            }
        });

        browseResumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    resumePathField.setText(fileChooser.getSelectedFile().getPath());
                }
            }
        });

        browseJobButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    jobDescPathField.setText(fileChooser.getSelectedFile().getPath());
                }
            }
        });
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Resume Path:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        inputPanel.add(resumePathField, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        inputPanel.add(browseResumeButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Job Description Path:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        inputPanel.add(jobDescPathField, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        inputPanel.add(browseJobButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.CENTER;
        inputPanel.add(analyzeButton, gbc);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Result area
        JScrollPane scrollPane = new JScrollPane(resultArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void analyzeResume() {
        String resumePath = resumePathField.getText();
        String jobDescPath = jobDescPathField.getText();

        if (resumePath.isEmpty() || jobDescPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please specify both resume and job description paths", 
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            resultArea.setText("Analyzing resume...\n");
            
            // Parse resume
            ResumeParser parser = new ResumeParser();
            String resumeText = parser.extractText(resumePath);
            
            if (resumeText.equals("Error extracting text")) {
                resultArea.append("Error: Could not extract text from resume. Please check the file path and format.\n");
                return;
            }
            
            // Extract resume components
            resultArea.append("\nExtracted skills from resume:\n");
            resultArea.append(String.join(", ", parser.extractSkills(resumeText)) + "\n");
            
            resultArea.append("\nExtracted education:\n");
            resultArea.append(String.join(", ", parser.extractEducation(resumeText)) + "\n");
            
            resultArea.append("\nExtracted experience:\n");
            resultArea.append(String.join(", ", parser.extractExperience(resumeText)) + "\n");
            
            // Match with job description
            resultArea.append("\nComparing with job description...\n");
            JobMatcher matcher = new JobMatcher();
            JobMatcher.MatchResult matchResult = matcher.compareWithJob(resumeText, jobDescPath);
            
            resultArea.append("\nMatch score: " + String.format("%.2f", matchResult.getScore()) + "%\n");
            resultArea.append("\nMatched keywords:\n");
            resultArea.append(String.join("\n", matchResult.getMatchedKeywords()) + "\n");
            
            resultArea.append("\nMissing important keywords:\n");
            resultArea.append(String.join("\n", matchResult.getMissingKeywords()) + "\n");
            
            // Generate suggestions
            resultArea.append("\nGenerating optimization suggestions...\n");
            ATSOptimizer optimizer = new ATSOptimizer();
            String jobDescription = new String(Files.readAllBytes(Paths.get(jobDescPath)));
            String suggestions = optimizer.suggestEnhancements(resumeText, jobDescription, matchResult.getMissingKeywords());
            resultArea.append("\n" + suggestions + "\n");
            
            // Generate report
            resultArea.append("\nGenerating comprehensive report...\n");
            ReportGenerator reportGen = new ReportGenerator();
            reportGen.generateReport(resumePath, matchResult, suggestions);
            resultArea.append("\nReport generated in the 'data' folder.\n");
            
        } catch (Exception ex) {
            ex.printStackTrace();
            resultArea.append("\nError during analysis: " + ex.getMessage() + "\n");
            JOptionPane.showMessageDialog(this, "Error during analysis: " + ex.getMessage(), 
                                         "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ResumeAnalyzerUI().setVisible(true);
            }
        });
    }
}
