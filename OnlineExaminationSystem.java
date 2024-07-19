import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OnlineExaminationSystem extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JButton registerButton;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private Timer timer;
    private JLabel questionLabel;
    private JRadioButton[] options;
    private JButton nextButton;
    private JButton backButton;
    private JButton submitButton;
    private JButton logoutButton;
    private User currentUser;
    private JCheckBox showAnswersCheckbox;

    private final JPanel loginPanel;

    public OnlineExaminationSystem() {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Login Panel
        loginPanel = new JPanel(new GridLayout(4, 2));
        loginPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        loginPanel.add(usernameField);

        loginPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        loginPanel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (authenticateUser(username, password)) {
                    currentUser = getUser(username);
                    displayExamFrame();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid credentials");
                }
            }
        });
        loginPanel.add(loginButton);

        registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = JOptionPane.showInputDialog("Enter new username:");
                String password = JOptionPane.showInputDialog("Enter new password:");

                if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                    users.add(new User(username, password, "User Profile"));
                    JOptionPane.showMessageDialog(null, "Registration successful! You can now log in.");
                } else {
                    JOptionPane.showMessageDialog(null, "Registration failed. Please enter valid username and password.");
                }
            }
        });
        loginPanel.add(registerButton);

        add(loginPanel);
    }

    private void displayExamFrame() {
        getContentPane().removeAll();
        setTitle("Online Examination - Time Remaining: 30:00");
        setSize(800, 600); // Increased frame size

        questions = getRandomQuestions(20);

        JPanel panel = new JPanel(new BorderLayout());
        questionLabel = new JLabel();
        panel.add(questionLabel, BorderLayout.NORTH);

        ButtonGroup group = new ButtonGroup();
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1));
        options = new JRadioButton[4];
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton();
            group.add(options[i]);
            optionsPanel.add(options[i]);
        }
        panel.add(optionsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentQuestionIndex > 0) {
                    currentQuestionIndex--;
                    loadQuestion(currentQuestionIndex);
                }
            }
        });
        buttonPanel.add(backButton);

        nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentQuestionIndex < questions.size() - 1) {
                    checkAnswer();
                    currentQuestionIndex++;
                    loadQuestion(currentQuestionIndex);
                } else {
                    JOptionPane.showMessageDialog(null, "No more questions. Submitting exam.");
                    endExam();
                }
            }
        });
        buttonPanel.add(nextButton);

        submitButton = new JButton("Submit Exam");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                endExam();
            }
        });
        buttonPanel.add(submitButton);

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                usernameField.setText("");
                passwordField.setText("");
                setTitle("Login");
                setSize(300, 200);
                setLocationRelativeTo(null);
                getContentPane().add(loginPanel);
            }
        });
        buttonPanel.add(logoutButton);

        showAnswersCheckbox = new JCheckBox("Show correct answers after submission");
        buttonPanel.add(showAnswersCheckbox);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        JMenuBar menuBar = new JMenuBar();
        JMenu addMenu = new JMenu("Add");
        JMenuItem addQuestionItem = new JMenuItem("Add Question");
        addQuestionItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddQuestionDialog();
            }
        });
        addMenu.add(addQuestionItem);
        menuBar.add(addMenu);
        setJMenuBar(menuBar);

        add(panel);
        loadQuestion(currentQuestionIndex);
        startTimer();

        revalidate();
        repaint();
    }

    private void loadQuestion(int index) {
        Question question = questions.get(index);
        questionLabel.setText(question.getText());
        List<String> optionsList = question.getOptions();
        for (int i = 0; i < optionsList.size(); i++) {
            options[i].setText(optionsList.get(i));
        }
    }

    private void checkAnswer() {
        Question question = questions.get(currentQuestionIndex);
        for (int i = 0; i < options.length; i++) {
            if (options[i].isSelected() && options[i].getText().equals(question.getCorrectOption())) {
                score += 2;
                break;
            }
        }
    }

    private void endExam() {
        timer.stop();
        int finalScore = score;
        String username = currentUser.getUsername();
        if (showAnswersCheckbox.isSelected()) {
            StringBuilder message = new StringBuilder("Exam Over! Your score: " + finalScore + "\n");
            message.append("Correct Answers:\n");
            for (Question question : questions) {
                message.append(question.getText()).append(": ").append(question.getCorrectOption()).append("\n");
            }
            message.append("\nUsername: ").append(username);
            JOptionPane.showMessageDialog(this, message.toString());
        } else {
            JOptionPane.showMessageDialog(this, "Exam Over! Your score: " + finalScore + "\nUsername: " + username);
        }
        dispose();
        usernameField.setText("");
        passwordField.setText("");
        setTitle("Login");
        setSize(300, 200);
        setLocationRelativeTo(null);
        getContentPane().add(loginPanel);
    }

    private void startTimer() {
        int examTime = 30 * 60; // 30 minutes
        timer = new Timer(1000, new ActionListener() {
            int timeRemaining = examTime;

            public void actionPerformed(ActionEvent e) {
                if (timeRemaining > 0) {
                    timeRemaining--;
                    setTitle("Online Examination - Time Remaining: " + timeRemaining / 60 + ":" + String.format("%02d", timeRemaining % 60));
                } else {
                    endExam();
                }
            }
        });
        timer.start();
    }

    private void showAddQuestionDialog() {
        JFrame parentFrame = new JFrame();

        JPanel panel = new JPanel(new GridLayout(4, 2));
        JTextField questionText = new JTextField();
        panel.add(new JLabel("Question Text:"));
        panel.add(questionText);

        JTextField option1Text = new JTextField();
        panel.add(new JLabel("Option 1:"));
        panel.add(option1Text);

        JTextField option2Text = new JTextField();
        panel.add(new JLabel("Option 2:"));
        panel.add(option2Text);

        JTextField option3Text = new JTextField();
        panel.add(new JLabel("Option 3:"));
        panel.add(option3Text);

        JTextField option4Text = new JTextField();
        panel.add(new JLabel("Option 4:"));
        panel.add(option4Text);

        JTextField correctOptionText = new JTextField();
        panel.add(new JLabel("Correct Option:"));
        panel.add(correctOptionText);

        int result = JOptionPane.showConfirmDialog(parentFrame, panel, "Add New Question",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String question = questionText.getText();
            List<String> options = new ArrayList<>();
            options.add(option1Text.getText());
            options.add(option2Text.getText());
            options.add(option3Text.getText());
            options.add(option4Text.getText());
            String correctOption = correctOptionText.getText();

            questionBank.add(new Question(question, options, correctOption));
            JOptionPane.showMessageDialog(parentFrame, "Question added successfully!");
        }
    }

    // Mock database methods
    private final  List<User> users = new ArrayList<>();
    private final List<Question> questionBank = new ArrayList<>();

    {
        users.add(new User("admin", "admin123", "Admin Profile"));
        questionBank.add(new Question("What is Java?", List.of("Platform", "anguage","Both", "None"), "Both"));
        questionBank.add(new Question("What is JVM?", List.of("Java Virtual Machine", "Java Verified Machine", "Java Very Machine", "None"), "Java Virtual Machine"));
        questionBank.add(new Question("What is JDK?", List.of("Java Development Kit", "Java Decompiler Kit", "Java Distribution Kit", "None"), "Java Development Kit"));
        questionBank.add(new Question("What is OOP?", List.of( "Object Oriented Process", "Object Oriented Programming", "Object Oriented Protocol", "None"), "Object Oriented Programming"));
        questionBank.add(new Question("What is SQL?", List.of( "Simple Query Language", "Secondary Query Language","Structured Query Language", "None"), "Structured Query Language"));
        questionBank.add(new Question("What is HTML?", List.of("Hyper Text Markup Language", "Hyperlinks and Text Markup Language", "Home Tool Markup Language", "None"), "Hyper Text Markup Language"));
        questionBank.add(new Question("What is CSS?", List.of("Colorful Style Sheets", "Computer Style Sheets", "Cascading Style Sheets", "None"), "Cascading Style Sheets"));
        questionBank.add(new Question("What is API?", List.of("Application Protocol Interface", "Application Programming Interface", "Application Process Interface", "None"), "Application Programming Interface"));
        questionBank.add(new Question("What is URL?", List.of("Uniform Resource Locator", "Uniform Resource Link", "Universal Resource Locator", "None"), "Uniform Resource Locator"));
        questionBank.add(new Question("What is JVM?", List.of( "Java Verified Machine", "Java Very Machine", "None", "Java Virtual Machine"), "Java Virtual Machine"));
        questionBank.add(new Question("What is JDK?", List.of("Java Development Kit", "Java Decompiler Kit", "Java Distribution Kit", "None"), "Java Development Kit"));
        questionBank.add(new Question("What is OOP?", List.of("Object Oriented Programming", "Object Oriented Process", "Object Oriented Protocol", "None"), "Object Oriented Programming"));
        questionBank.add(new Question("What is SQL?", List.of("Structured Query Language", "Simple Query Language", "Secondary Query Language", "None"), "Structured Query Language"));
        questionBank.add(new Question("What is HTML?", List.of("Hyper Text Markup Language", "Hyperlinks and Text Markup Language", "Home Tool Markup Language", "None"), "Hyper Text Markup Language"));
        //add more quetions...
    }

    private boolean authenticateUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    private User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    private List<Question> getRandomQuestions(int number) {
        Collections.shuffle(questionBank);
        return questionBank.subList(0, Math.min(number, questionBank.size()));
    }

    // Inner classes for User and Question
    class User {
        private final String username;
        private String password;
        private String profile;

        public User(String username, String password, String profile) {
            this.username = username;
            this.password = password;
            this.profile = profile;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getProfile() {
            return profile;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }
    }

    class Question {
        private final String text;
        private final List<String> options;
        private final String correctOption;

        public Question(String text, List<String> options, String correctOption) {
            this.text = text;
            this.options = options;
            this.correctOption = correctOption;
        }

        public String getText() {
            return text;
        }

        public List<String> getOptions() {
            return options;
        }

        public String getCorrectOption() {
            return correctOption;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                OnlineExaminationSystem frame = new OnlineExaminationSystem();
                frame.setVisible(true);
            }
        });
    }
}
