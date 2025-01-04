package apartmentAssistent;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class Application extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private Connection connection;

    static String userName;
    boolean isAdmin = false;
    static int userId;
    static int specialistId;

    public Application() throws SQLException {
        super("Welcome to Apartment Assistant");
        this.setSize(430, 610);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        mainPanel = new JPanel(new CardLayout());
        cardLayout = (CardLayout) mainPanel.getLayout();

        JPanel loginPanel = createLoginPanel();
        mainPanel.add(loginPanel, "LoginPanel");
        this.add(mainPanel);

        Properties config = Config.loadProperties();
        this.connection = DriverManager.getConnection(
                config.getProperty("database.url"),
                config.getProperty("database.login"),
                config.getProperty("database.password"));
    }

    //хелпер создания кнопок в окнах
    private JButton createLoginButton(String text) {
        JButton button = new JButton(text) {
            private final int dotSize = 2;
            private final int spacing = 1;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arcWidth = 30;
                int arcHeight = 30;

                g2d.setColor(Color.BLACK);
                g2d.fillRoundRect(5, 5, getWidth() - 5, getHeight() - 5, arcWidth, arcHeight);

                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(118, 72, 255),
                        getWidth(), getHeight(), new Color(23, 0, 122)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);

                g2d.setColor(new Color(30, 0, 63));
                g2d.setStroke(new BasicStroke(4));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, arcWidth, arcHeight);

                Color startColor = new Color(30, 0, 63, 0);
                Color endColor = new Color(30, 0, 63, 150);

                for (int i = 0; i < getWidth(); i += dotSize + spacing) {
                    for (int j = 0; j < getHeight(); j += dotSize + spacing) {
                        int x = i + j;
                        int y = j;

                        if (x < getWidth()) {
                            if (isPointInsideRoundedRect(x, y, getWidth(), getHeight(), arcWidth, arcHeight)) {
                                float fraction = (float) x / getWidth();
                                Color interpolatedColor = interpolateColor(startColor, endColor, fraction);
                                g2d.setColor(interpolatedColor);
                                g2d.fillOval(x, y, dotSize, dotSize);
                            }
                        }
                    }
                }
                super.paintComponent(g);
            }

            private boolean isPointInsideRoundedRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
                java.awt.geom.RoundRectangle2D roundedRect = new java.awt.geom.RoundRectangle2D.Float(
                        0, 0, width, height, arcWidth, arcHeight
                );
                return roundedRect.contains(x, y);
            }

            private Color interpolateColor(Color butt_color1, Color butt_color2, float fraction) {
                float r = butt_color1.getRed() + (butt_color2.getRed() - butt_color1.getRed()) * fraction;
                float g = butt_color1.getGreen() + (butt_color2.getGreen() - butt_color1.getGreen()) * fraction;
                float b = butt_color1.getBlue() + (butt_color2.getBlue() - butt_color1.getBlue()) * fraction;
                float a = butt_color1.getAlpha() + (butt_color2.getAlpha() - butt_color1.getAlpha()) * fraction;
                return new Color((int) r, (int) g, (int) b, (int) a);
            }
        };

        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setFont(new Font("JetBrains Mono", Font.PLAIN, 28));
        button.setForeground(Color.WHITE);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setPreferredSize(new Dimension(550, 100));

        button.addActionListener(e -> {
            if (text.equals("LOG IN")) {
                logToAccount();
            } else if (text.equals("SIGN UP")) {
                createAccount();
            }
        });

        return button;
    }


    // самое первое окно, в котором пользователь входит\создает ак
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(255, 0, 212);
                Color color2 = new Color(122, 0, 255);
                Color color3 = new Color(255, 122, 236);
                Color color4 = new Color(0, 3, 255);

                GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                BufferedImage noiseImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                for (int x = 0; x < getWidth(); x++) {
                    for (int y = 0; y < getHeight(); y++) {
                        int noise = (int) (Math.random() * 50);
                        int alpha = 60;
                        int rgba = new Color(noise, noise, noise, alpha).getRGB();
                        noiseImage.setRGB(x, y, rgba);
                    }
                }

                g2d.drawImage(noiseImage, 0, 0, null);

                int dotSize = 2;
                int spacing = 3;

                for (int x = 0; x < getWidth(); x += spacing) {
                    for (int y = 0; y < getHeight(); y += spacing) {
                        float fraction = (float) (x + y) / (getWidth() + getHeight());
                        Color dotColor = interpolateColor(color3, color4, fraction);
                        g2d.setColor(dotColor);
                        g2d.fillOval(x, y, dotSize, dotSize);
                    }
                }

                int secondDotSize = 12;
                int secondDotSpacing = 7;

                float fadeOutFactor = 2.2f;

                for (int x = 0; x < getWidth(); x += secondDotSpacing) {
                    for (int y = 0; y < getHeight(); y += secondDotSpacing) {
                        float distanceFraction = (float) (x + y) / (fadeOutFactor * (getWidth() + getHeight()));
                        distanceFraction = Math.min(distanceFraction, 1);
                        int alpha = (int) (255 * distanceFraction);
                        alpha = Math.max(alpha, 0);

                        Color dotColor = new Color(0, 2, 20, alpha);
                        g2d.setColor(dotColor);
                        g2d.fillOval(x, y, secondDotSize, secondDotSize);
                    }
                }
            }
            private Color interpolateColor(Color color3, Color color4, float fraction) {
                float r = color3.getRed() + (color4.getRed() - color3.getRed()) * fraction;
                float g = color3.getGreen() + (color4.getGreen() - color3.getGreen()) * fraction;
                float b = color3.getBlue() + (color4.getBlue() - color3.getBlue()) * fraction;
                return new Color((int) r, (int) g, (int) b);
            }
        };

        panel.setPreferredSize(new Dimension(450, 600));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setOpaque(false);

        JLabel label = new JLabel("WELCOME!", JLabel.CENTER);
        label.setFont(new Font("JetBrains Mono", Font.PLAIN, 38));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 0, 0),
                BorderFactory.createLineBorder(Color.BLACK, 0)
        ));

        centerContainer.add(label);

        centerContainer.add(Box.createRigidArea(new Dimension(0, 140)));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        JButton loginButton = createLoginButton("LOG IN");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(5, 20))); // Smaller gap between buttons

        JButton createAccountButton = createLoginButton("SIGN UP");
        createAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(createAccountButton);

        centerContainer.add(buttonPanel);

        panel.add(centerContainer, BorderLayout.CENTER);

        return panel;
    }

    //создание аккаунта пользователя
    private void createAccount() {
        JFrame logFrame = new JFrame("SIGN UP");
        logFrame.setSize(400, 200);
        logFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        logFrame.setLocationRelativeTo(this);

        Font customFont = new Font("JetBrains Mono", Font.PLAIN, 14);

        JPanel inputPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(148, 0, 255),
                        getWidth(), getHeight(), new Color(0, 0, 0)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(new Color(255, 133, 245, 50));
                int dotSize = 2;
                int spacing = 2;

                for (int x = 0; x < getWidth(); x += spacing) {
                    for (int y = 0; y < getHeight(); y += spacing) {
                        g2d.fillOval(x, y, dotSize, dotSize);
                    }
                }
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel loginLabel = new JLabel("Login:");
        loginLabel.setForeground(Color.WHITE);
        loginLabel.setFont(customFont);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(customFont);

        JTextField loginField = new JTextField(15);
        loginField.setFont(customFont);
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(customFont);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(loginLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(loginField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(passwordField, gbc);

        JButton addButton = new JButton("Sign Up");
        addButton.setFont(customFont); // Apply font
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = loginField.getText();
                String password = new String(passwordField.getPassword());

                if (login.trim().isEmpty() || password.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Fill in all the fields");
                    return;
                }
                try {
                    if (checkLoginExists(login)) {
                        JOptionPane.showMessageDialog(null, "User with this login already exists!");
                        return;
                    }
                    PreparedStatement statement = connection.prepareStatement(
                            "INSERT INTO users (login, password) VALUES(?,?)"
                    );
                    statement.setString(1, login);
                    statement.setString(2, HashUtil.hash(password));
                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "The account has been successfully created!");
                        logFrame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error occurred when creating an account", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error occurred when creating an account: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);

        logFrame.add(inputPanel, BorderLayout.CENTER); // Place inputPanel in the center
        logFrame.add(buttonPanel, BorderLayout.SOUTH);
        logFrame.setVisible(true);
    }

    //проверка наличия акка по логину
    private boolean checkLoginExists(String login) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE login = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        }
    }

    //вход в ак по логину и паролю
    private void logToAccount() {
        JFrame logFrame = new JFrame("LOG IN");
        logFrame.setSize(400, 200);
        logFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        logFrame.setLocationRelativeTo(this);

        Font customFont = new Font("JetBrains Mono", Font.PLAIN, 14);

        JPanel inputPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(0, 13, 82),
                        getWidth(), getHeight(), new Color(255, 0, 144)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(new Color(153, 255, 142, 113));
                int dotSize = 1;
                int spacing = 2;

                for (int x = 0; x < getWidth(); x += spacing) {
                    for (int y = 0; y < getHeight(); y += spacing) {
                        g2d.fillOval(x, y, dotSize, dotSize);
                    }
                }
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel loginLabel = new JLabel("Login:");
        loginLabel.setForeground(Color.WHITE);
        loginLabel.setFont(customFont);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(customFont);

        JTextField loginField = new JTextField(15);
        loginField.setFont(customFont);
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(customFont);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(loginLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(loginField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(passwordField, gbc);

        JButton addButton = new JButton("Log In");
        addButton.setFont(customFont);
        addButton.addActionListener(e -> {
            String login = loginField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            if (login.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Fill in all the fields", "Input error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                String checkUserQuery = "SELECT password, is_admin FROM users WHERE login = ?";
                try (PreparedStatement checkUserStatement = connection.prepareStatement(checkUserQuery)) {
                    checkUserStatement.setString(1, login);

                    try (ResultSet userResult = checkUserStatement.executeQuery()) {
                        if (userResult.next()) {
                            String storedPassword = userResult.getString("password");
                            if (HashUtil.check(password, storedPassword)) {
                                isAdmin = userResult.getBoolean("is_admin");
                                userName = login;
                                logFrame.setVisible(false);
                                if (isAdmin) {
                                    JOptionPane.showMessageDialog(null, "Connected as administrator with login " + userName);
                                    showAdminPanel();
                                } else {
                                    checkSpecialistStatus(login);
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Invalid password", "Input error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "No account found with this login", "Logging error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error when logging into the account: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);

        logFrame.add(inputPanel, BorderLayout.CENTER);
        logFrame.add(buttonPanel, BorderLayout.SOUTH);
        logFrame.setVisible(true);
    }

    //проверка специалист или пользователь
    private void checkSpecialistStatus(String login) {
        try {
            String findWorkerQuery = "SELECT id FROM specialists WHERE lower(login) = lower(?)";
            login = login.trim();
            System.out.println("Final login value (trimmed): '" + login + "'");
            try (PreparedStatement findWorkerStatement = connection.prepareStatement(findWorkerQuery)) {
                findWorkerStatement.setString(1, login);
                try (ResultSet specialistResult = findWorkerStatement.executeQuery()) {
                    if (specialistResult.next()) {
                        specialistId = specialistResult.getInt("id");
                        JOptionPane.showMessageDialog(null, "Connected as specialist with login " + login + " (ID: " + specialistId + ")");
                        showSpecialistPanel();
                    } else {
                        try (PreparedStatement findUserIdStatement = connection.prepareStatement(
                                "SELECT id FROM users WHERE login = ?")) {
                            findUserIdStatement.setString(1, login);
                            try (ResultSet rs = findUserIdStatement.executeQuery()) {
                                if (rs.next()) {
                                    userId = rs.getInt("id");
                                    System.out.println(userId);
                                    JOptionPane.showMessageDialog(null, "Connected as user with login " + login + " (ID: " + userId + ")");
                                    showUserPanel();
                                } else {
                                    JOptionPane.showMessageDialog(null, "No user found with login " + login);
                                }
                            }
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Database error while finding user ID");
                            ex.printStackTrace();
                        }
                        showUserPanel();
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error when verifying specialist: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //добавление окон для специалиста
    private void showSpecialistPanel(){
        mainPanel.removeAll();
        JPanel loginPanel =createLoginPanel();
        mainPanel.add(loginPanel,"Login Panel");
        JPanel mainSpecialistPanel=mainSpecialistPanel();
        mainPanel.add(mainSpecialistPanel, "Main Specialist Panel");
        cardLayout.show(mainPanel,"Main Specialist Panel");
    }

    //добавление окон для админа
    private void showAdminPanel() {
        mainPanel.removeAll();
        JPanel loginPanel = createLoginPanel();
        mainPanel.add(loginPanel, "LoginPanel");
        JPanel mainAdminPanel = mainAdminPanel();
        mainPanel.add(mainAdminPanel, "MainAdminPanel");
        cardLayout.show(mainPanel, "MainAdminPanel");
    }

    //добавление окон для пользователя
    private void showUserPanel() {
        mainPanel.removeAll();
        JPanel loginPanel = createLoginPanel();
        mainPanel.add(loginPanel, "LoginPanel");
        JPanel mainUserPanel = mainUserPanel();
        mainPanel.add(mainUserPanel, "MainUserPanel");
        cardLayout.show(mainPanel, "MainUserPanel");
    }

    //главное меню специалиста
    private JPanel mainSpecialistPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(230, 230, 250));

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                GradientPaint gradient = new GradientPaint(0, 0, new Color(255, 77, 117),
                        getWidth(), getHeight(), new Color(131, 0, 248));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                int dotSize = 5;
                int spacing = 2;
                for (int x = 0; x < getWidth(); x += spacing) {
                    for (int y = 0; y < getHeight(); y += spacing) {
                        g2d.setColor(new Color(249, 168, 255, 60));
                        g2d.fillOval(x, y, dotSize, dotSize);
                    }
                }

                g2d.dispose();
            }
        };
        backgroundPanel.setLayout(null);

        // Title label with shadow and outline
        JLabel titleLabel = new JLabel("Hello! Welcome to Assistant!", JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                String text = getText();
                FontMetrics fm = g2d.getFontMetrics(getFont());

                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;

                // Shadow effect
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.drawString(text, x + 2, y + 2);

                // Outline
                g2d.setColor(Color.BLACK);
                g2d.drawString(text, x - 1, y - 1);
                g2d.drawString(text, x + 1, y - 1);
                g2d.drawString(text, x - 1, y + 1);
                g2d.drawString(text, x + 1, y + 1);

                // Actual text
                g2d.setColor(Color.WHITE);
                g2d.drawString(text, x, y);

                g2d.dispose();
            }
        };
        titleLabel.setFont(new Font("JetBrains Mono", Font.BOLD | Font.ITALIC, 25));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(false);
        titleLabel.setBounds(0, 20, 800, 40);
        backgroundPanel.add(titleLabel);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(50, 100, 500, 400); // Center horizontally, small margin from left

        buttonPanel.add(Box.createVerticalGlue()); // Add spacing above buttons

        buttonPanel.add(createStyledButton("View your outstanding orders", e -> viewData("Orders", "select * from service_requests where specialist_id=" + specialistId)));
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(createStyledButton("Send the completed order id", e -> completeOrder()));
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(createStyledButton("View the orders you can complete", e -> {
            String query = String.format(
                    "select sr.*from service_requests sr where sr.service_id = any (select unnest(s.service_id) from specialists s " +
                            "where s.id = %d)", specialistId);
            viewData("Orders", query);
        }));
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(createStyledButton("Make an action with list of services", e -> changeSpecialistInOrder()));

        buttonPanel.add(Box.createVerticalGlue());

        backgroundPanel.add(buttonPanel);
        mainPanel.add(backgroundPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    //смена специалиста в заказе
    private void changeSpecialistInOrder() {
        JFrame changeSpecialistFrame = new JFrame("Change Specialist in Order");
        changeSpecialistFrame.setSize(400, 200);
        changeSpecialistFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        changeSpecialistFrame.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        JTextField requestIdField = new JTextField();

        inputPanel.add(new JLabel("Enter the ID of the service request (must be > 0):"));
        inputPanel.add(requestIdField);

        JButton updateButton = new JButton("Update Specialist");
        updateButton.addActionListener(e -> {
            String requestIdText = requestIdField.getText().trim();
            if (requestIdText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a valid request ID");
                return;
            }
            int requestId;
            try {
                requestId = Integer.parseInt(requestIdText);
                if (requestId <= 0) {
                    JOptionPane.showMessageDialog(null, "The request ID must be greater than 0");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "The request ID must be a numeric value");
                return;
            }

            try {
                connection.setAutoCommit(false);
                int serviceId = -1;
                try (PreparedStatement checkRequestStatement = connection.prepareStatement(
                        "SELECT service_id, specialist_id FROM service_requests WHERE id = ?")) {
                    checkRequestStatement.setInt(1, requestId);
                    try (ResultSet rs = checkRequestStatement.executeQuery()) {
                        if (rs.next()) {
                            serviceId = rs.getInt("service_id");
                            Integer currentSpecialistId = rs.getObject("specialist_id", Integer.class);
                            if (currentSpecialistId != null) {
                                JOptionPane.showMessageDialog(null, "This order already has a specialist assigned.");
                                return;
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "No service request found with the given ID.");
                            return;
                        }
                    }
                }

                boolean isServiceSupported = false;
                try (PreparedStatement checkSpecialistStatement = connection.prepareStatement(
                        "SELECT 1 FROM specialists WHERE id = ? AND ? = ANY (service_id)")) {
                    checkSpecialistStatement.setInt(1, specialistId);
                    checkSpecialistStatement.setInt(2, serviceId);

                    try (ResultSet rs = checkSpecialistStatement.executeQuery()) {
                        if (rs.next()) {
                            isServiceSupported = true;
                        }
                    }
                }

                if (!isServiceSupported) {
                    JOptionPane.showMessageDialog(null, "The specialist cannot complete this service.");
                    return;
                }

                try (PreparedStatement updateStatement = connection.prepareStatement(
                        "UPDATE service_requests SET specialist_id = ? WHERE id = ?")) {
                    updateStatement.setInt(1, specialistId);
                    updateStatement.setInt(2, requestId);
                    int rowsUpdated = updateStatement.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(null, "Specialist has been successfully assigned to the order.");
                        connection.commit();
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to update the order.");
                        connection.rollback();
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Database connection error");
                ex.printStackTrace();
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(updateButton);
        changeSpecialistFrame.add(inputPanel, BorderLayout.CENTER);
        changeSpecialistFrame.add(buttonPanel, BorderLayout.SOUTH);
        changeSpecialistFrame.setVisible(true);
    }

    //отобразить, что заказ выполнен
    private void completeOrder() {
        String input = JOptionPane.showInputDialog(null,
                "Enter the ID of the order to complete (must be > 0):",
                "Complete Order",
                JOptionPane.QUESTION_MESSAGE);

        if (input == null) {
            JOptionPane.showMessageDialog(null, "Action canceled.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            int orderId = Integer.parseInt(input.trim());
            if (orderId <= 0) {
                JOptionPane.showMessageDialog(null,
                        "The ID must be a positive number greater than 0.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String requestDate = null;
            int serviceId = -1;
            try (PreparedStatement findStmt = connection.prepareStatement(
                    "SELECT request_date, service_id FROM service_requests WHERE id = ?")) {
                findStmt.setInt(1, orderId);
                try (ResultSet rs = findStmt.executeQuery()) {
                    if (rs.next()) {
                        requestDate = rs.getDate("request_date").toString(); // Преобразуем дату в строку
                        serviceId = rs.getInt("service_id");
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "No order with ID " + orderId + " was found.",
                                "Order Not Found",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            int confirm = JOptionPane.showConfirmDialog(null,
                    "Order Details:\nRequest Date: " + requestDate + "\nService ID: " + serviceId +
                            "\n\nDo you really want to delete this order?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(null, "Deletion canceled.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            try (PreparedStatement deleteStmt = connection.prepareStatement("DELETE FROM service_requests WHERE id = ?")) {
                deleteStmt.setInt(1, orderId);
                int rowsAffected = deleteStmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null,
                            "Order with ID " + orderId + " was successfully completed and removed.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Failed to delete order with ID " + orderId + ".",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

            String insertQuery = "INSERT INTO completed_orders (order_creation_date, order_completion_date, user_comment, service_id, specialist_id) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setDate(1, Date.valueOf(requestDate)); // order_creation_date
                insertStatement.setDate(2, new Date(System.currentTimeMillis())); // order_completion_date
                insertStatement.setString(3, "No comments (release soon)"); // user_comment
                insertStatement.setInt(4, serviceId); // service_id
                insertStatement.setInt(5, specialistId); // specialist_id
                insertStatement.executeUpdate();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null,
                    "Invalid input. Please enter a valid positive integer.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "An error occurred while processing the order: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    //главное окно админа
    private JPanel mainAdminPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(230, 230, 250));

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                GradientPaint gradient = new GradientPaint(0, 0, new Color(255, 150, 150),
                        getWidth(), getHeight(), new Color(120, 80, 200));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                int dotSize = 2;
                int spacing = 2;
                for (int x = 0; x < getWidth(); x += spacing) {
                    for (int y = 0; y < getHeight(); y += spacing) {
                        g2d.setColor(new Color(249, 168, 255, 60));
                        g2d.fillOval(x, y, dotSize, dotSize);
                    }
                }

                g2d.dispose();
            }
        };
        backgroundPanel.setLayout(null);


        JLabel titleLabel = new JLabel("Welcome to panel!", JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                String text = getText();
                FontMetrics fm = g2d.getFontMetrics(getFont());

                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;

                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.drawString(text, x + 2, y + 2);

                g2d.setColor(Color.BLACK);
                g2d.drawString(text, x - 1, y - 1);
                g2d.drawString(text, x + 1, y - 1);
                g2d.drawString(text, x - 1, y + 1);
                g2d.drawString(text, x + 1, y + 1);

                g2d.setColor(Color.WHITE);
                g2d.drawString(text, x, y);

                g2d.dispose();
            }
        };
        titleLabel.setFont(new Font("JetBrains Mono", Font.BOLD | Font.ITALIC, 25));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(false);
        titleLabel.setBounds(0, 20, 800, 40);
        backgroundPanel.add(titleLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 2, 15, 15)); // Adjusted for admin buttons
        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(50, 100, 700, 500); // Center horizontally, add margin

        buttonPanel.add(createStyledButton("View the list of orders", e -> viewData("Orders", "select * from service_requests")));
        buttonPanel.add(createStyledButton("Make an action with list of orders", e -> modifyOrdersData()));

        buttonPanel.add(createStyledButton("View the list of services", e -> viewData("Services", "select * from services")));
        buttonPanel.add(createStyledButton("Make an action with list of services", e -> modifyServicesData()));

        buttonPanel.add(createStyledButton("View the list of users", e -> viewData("Users", "select id, password, login from users where is_admin = false;")));
        buttonPanel.add(createStyledButton("Make an action with list of users", e -> modifyUserData()));

        buttonPanel.add(createStyledButton("View the list of workers", e -> viewData("Specialists", "select * from specialists")));
        buttonPanel.add(createStyledButton("Delete workers from db", e -> modifyWorkersData()));

        buttonPanel.add(createStyledButton("View the list of administrators", e -> viewData("Users", "select id, password, login from users where is_admin = true;")));
        buttonPanel.add(createStyledButton("Delete administrators from db", e -> modifyAdminsData()));

        buttonPanel.add(createStyledButton("Unfulfilled orders", e -> viewData("Not completed", "select service_id as \"Service id\", specialist_id as \"Specialist id\", request_date as \"Request date\" from service_requests;")));
        buttonPanel.add(createStyledButton("Completed orders", e -> viewData("Completed", "select service_id as \"Service id\", specialist_id as \"Specialist id\", order_creation_date as \"Request date\", order_completion_date as \"Completion date\" from completed_orders;")));

        backgroundPanel.add(buttonPanel);
        mainPanel.add(backgroundPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    //изменение users
    private void modifyUserData() {
        JFrame updateFrame = new JFrame("Change services data");
        updateFrame.setSize(600, 400);
        updateFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        updateFrame.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField idField = new JTextField();

        inputPanel.add(new JLabel("Id:"));
        inputPanel.add(idField);

        JButton makeWorkerButton = new JButton("Give worker/specialist status");
        makeWorkerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText().trim();
                if (id.isEmpty() || !id.matches("\\d+") || Integer.parseInt(id) <= 0) {
                    JOptionPane.showMessageDialog(null, "Enter a valid positive ID for the modification", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String fullName = JOptionPane.showInputDialog("Enter full name:");
                if (fullName == null || fullName.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Full name cannot be empty.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String servicesInput = JOptionPane.showInputDialog("Enter service ids (comma separated):");
                if (servicesInput == null || servicesInput.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "At least one service must be entered.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String[] servicesArray = servicesInput.split(",");
                java.util.List<Integer> serviceIds = new ArrayList<>();
                for (String service : servicesArray) {
                    try {
                        serviceIds.add(Integer.parseInt(service.trim()));
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid service ID format", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                String login = null;
                String queryLogin = "SELECT login FROM users WHERE id = ?";  // SQL-запрос для получения логина
                try (PreparedStatement loginStatement = connection.prepareStatement(queryLogin)) {
                    loginStatement.setInt(1, Integer.parseInt(id));
                    ResultSet resultSet = loginStatement.executeQuery();
                    if (resultSet.next()) {
                        login = resultSet.getString("login");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Database error when fetching login", "ERROR", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                    return;
                }

                if (login == null) {
                    JOptionPane.showMessageDialog(null, "No user found with the specified ID", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String query = "INSERT INTO specialists (full_name, service_id, login) VALUES (?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, fullName);
                    statement.setArray(2, connection.createArrayOf("integer", serviceIds.toArray()));
                    statement.setString(3, login);

                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Specialist status updated successfully.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to update specialist status.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Database error while inserting specialist", "ERROR", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        JButton makeAdminButton=new JButton("Give administrator status");
        makeAdminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText().trim();
                if (id.isEmpty() || !id.matches("\\d+") || Integer.parseInt(id) <= 0) {
                    JOptionPane.showMessageDialog(null, "Enter a valid positive ID for the modification", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                StringBuilder queryBuilder = new StringBuilder("update users set is_admin=true where id=?;");
                try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
                    statement.setInt(1,Integer.parseInt(id));
                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "User has administrator status");
                        idField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, "An entry with the specified parameters was not found");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Database connection error", "ERROR", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText().trim();
                if (id.isEmpty() || !id.matches("\\d+") || Integer.parseInt(id) <= 0) {
                    JOptionPane.showMessageDialog(null, "Enter a valid positive ID for the modification", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                StringBuilder queryBuilder = new StringBuilder("DELETE FROM users WHERE id= ? ");
                try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
                   statement.setInt(1,Integer.parseInt(id));
                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "The record was successfully deleted from the services table");
                        idField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, "An entry with the specified parameters was not found");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Database connection error", "ERROR", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText().trim();
                String newLogin = JOptionPane.showInputDialog("Enter the new login (leave empty to keep current):");
                String newPassword = JOptionPane.showInputDialog("Enter the new password (leave empty to keep current):");

                if (id.isEmpty() || !id.matches("\\d+") || Integer.parseInt(id) <= 0) {
                    JOptionPane.showMessageDialog(null, "Enter a valid positive ID for the modification", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if ((newLogin == null || newLogin.trim().isEmpty()) && (newPassword == null || newPassword.trim().isEmpty())) {
                    JOptionPane.showMessageDialog(null, "You didn't enter any new data, so no changes were made", "INFO", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                StringBuilder queryBuilder = new StringBuilder("UPDATE users SET ");
                boolean hasChanges = false;

                if (newLogin != null && !newLogin.trim().isEmpty()) {
                    if (!newLogin.matches("[a-zA-Z0-9_]+")) {
                        JOptionPane.showMessageDialog(null, "Login must contain only letters, numbers, or underscores", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    queryBuilder.append("login = ?, ");
                    hasChanges = true;
                }

                if (newPassword != null && !newPassword.trim().isEmpty()) {
                    queryBuilder.append("password = ?, ");
                    hasChanges = true;
                }

                if (!hasChanges) {
                    JOptionPane.showMessageDialog(null, "You didn't enter any valid data to update", "INFO", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                queryBuilder.setLength(queryBuilder.length() - 2);
                queryBuilder.append(" WHERE id = ?");

                try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
                    int parameterIndex = 1;
                    if (newLogin != null && !newLogin.trim().isEmpty()) {
                        statement.setString(parameterIndex++, newLogin);
                    }
                    if (newPassword != null && !newPassword.trim().isEmpty()) {
                        statement.setString(parameterIndex++, HashUtil.hash(newPassword));
                    }
                    statement.setInt(parameterIndex, Integer.parseInt(id));

                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "The user's data was successfully updated");
                        idField.setText(""); // Очищаем поле ID
                    } else {
                        JOptionPane.showMessageDialog(null, "No user found with the specified ID");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Database connection error", "ERROR", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(makeAdminButton);
        buttonPanel.add(makeWorkerButton);
        updateFrame.add(inputPanel, BorderLayout.NORTH);
        updateFrame.add(buttonPanel, BorderLayout.SOUTH);
        updateFrame.setVisible(true);
    }

    //изменение админов из users
    private void modifyAdminsData() {
        JFrame updateFrame = new JFrame("Change services data");
        updateFrame.setSize(400, 200);
        updateFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        updateFrame.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();

        inputPanel.add(new JLabel("Id:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();
                boolean hasName=false;
                if (id.isEmpty() && name.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Fill in at least one field to delete", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                StringBuilder queryBuilder = new StringBuilder("DELETE FROM services WHERE ");
                if (!id.isEmpty()) {
                    if (!id.matches("\\d+") || Integer.parseInt(id) <= 0) {
                        JOptionPane.showMessageDialog(null, "ID must be a positive integer", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    queryBuilder.append("id = ? ");
                }

                if (!name.isEmpty()) {
                    if (!name.matches("[a-zA-Zа-яА-ЯёЁ0-9\\s\\-]+")) {
                        JOptionPane.showMessageDialog(null, "Name must contain only letters, spaces, or dashes", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    queryBuilder.append("and name = ?");
                }

                try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
                    int paramIndex = 1;
                    statement.setInt(paramIndex++, Integer.parseInt(id));
                    if (hasName) {
                        statement.setString(paramIndex, name);
                    }

                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "The record was successfully deleted from the services table");
                        idField.setText("");
                        nameField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, "An entry with the specified parameters was not found");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Database connection error", "ERROR", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();

                if (id.isEmpty() || !id.matches("\\d+") || Integer.parseInt(id) <= 0) {
                    JOptionPane.showMessageDialog(null, "Enter a valid positive ID for the modification", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Fill in the name field to modify data", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String query = "UPDATE services SET name = ? WHERE id = ?";

                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, name);
                    statement.setInt(2, Integer.parseInt(id));

                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "The record was successfully updated");
                        idField.setText("");
                        nameField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, "No entry found with the specified ID");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Database connection error", "ERROR", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        updateFrame.add(inputPanel, BorderLayout.NORTH);
        updateFrame.add(buttonPanel, BorderLayout.SOUTH);
        updateFrame.setVisible(true);
    }

    //изменение specialists
    private void modifyWorkersData() {
        JFrame updateFrame = new JFrame("Change specialists/workers data");
        updateFrame.setSize(400, 200);
        updateFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        updateFrame.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField serviceIdField= new JTextField();
        JTextField loginField =new JTextField();

        inputPanel.add(new JLabel("Id:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Full name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Service id:"));
        inputPanel.add(serviceIdField);
        inputPanel.add(new JLabel("Login:"));
        inputPanel.add(loginField);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();
                String serviceId= serviceIdField.getText().trim();
                String login= loginField.getText().trim();
                if (id.isEmpty() && name.isEmpty()&&serviceId.isEmpty()&&login.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Fill in at least one field to delete", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                StringBuilder queryBuilder = new StringBuilder("DELETE FROM specialists WHERE ");
                if (!id.isEmpty()) {
                    if (!id.matches("\\d+") || Integer.parseInt(id) <= 0) {
                        JOptionPane.showMessageDialog(null, "ID must be a positive integer", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    queryBuilder.append("id = ? and ");
                }

                if (!name.isEmpty()) {
                    if (!name.matches("[a-zA-Zа-яА-ЯёЁ\\s\\-]+")) {
                        JOptionPane.showMessageDialog(null, "Name must contain only letters, spaces, or dashes", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    queryBuilder.append("name = ? and ");
                }

                if (!serviceId.isEmpty()){
                    if (!serviceId.matches("\\d+")||Integer.parseInt(serviceId)<=0){
                        JOptionPane.showMessageDialog(null, "Service ID must be a positive integer", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    queryBuilder.append("service_id=? and ");
                }

                if (!login.isEmpty()){
                    queryBuilder.append("login=? and ");
                }

                queryBuilder.setLength(queryBuilder.length() - 4);

                try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
                    int paramIndex = 1;
                    if (!id.isEmpty()){
                        statement.setInt(paramIndex++, Integer.valueOf(id));
                    }
                    if (!name.isEmpty()){
                        statement.setString(paramIndex++, name);
                    }
                    if (!serviceId.isEmpty()){
                        statement.setInt(paramIndex++, Integer.valueOf(serviceId));
                    }
                    if (!login.isEmpty()){
                        statement.setString(paramIndex, login);
                    }
                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "The record was successfully deleted from the services table");
                        idField.setText("");
                        nameField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, "An entry with the specified parameters was not found");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Database connection error", "ERROR", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(deleteButton);
        updateFrame.add(inputPanel, BorderLayout.NORTH);
        updateFrame.add(buttonPanel, BorderLayout.SOUTH);
        updateFrame.setVisible(true);
    }

    //изменение списка услуг
    private void modifyServicesData() {
        JFrame updateFrame = new JFrame("Change services data");
        updateFrame.setSize(400, 200);
        updateFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        updateFrame.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();

        inputPanel.add(new JLabel("Id:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();

                if (id.isEmpty() && name.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Fill in at least one field to delete", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                StringBuilder queryBuilder = new StringBuilder("DELETE FROM services WHERE ");
                boolean hasId = false;
                boolean hasName = false;

                if (!id.isEmpty()) {
                    if (!id.matches("\\d+") || Integer.parseInt(id) <= 0) {
                        JOptionPane.showMessageDialog(null, "ID must be a positive integer", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    queryBuilder.append("id = ? ");
                    hasId = true;
                }

                if (!name.isEmpty()) {
                    if (!name.matches("[a-zA-Zа-яА-ЯёЁ0-9\\s\\-]+")) {
                        JOptionPane.showMessageDialog(null, "Name must contain only letters, spaces, or dashes", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (hasId) queryBuilder.append("AND ");
                    queryBuilder.append("name = ? ");
                    hasName = true;
                }

                try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
                    int paramIndex = 1;
                    if (hasId) {
                        statement.setInt(paramIndex++, Integer.parseInt(id));
                    }
                    if (hasName) {
                        statement.setString(paramIndex, name);
                    }

                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "The record was successfully deleted from the services table");
                        idField.setText("");
                        nameField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, "An entry with the specified parameters was not found");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Database connection error", "ERROR", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();

                if (id.isEmpty() || !id.matches("\\d+") || Integer.parseInt(id) <= 0) {
                    JOptionPane.showMessageDialog(null, "Enter a valid positive ID for the modification", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Fill in the name field to modify data", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String query = "UPDATE services SET name = ? WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, name);
                    statement.setInt(2, Integer.parseInt(id));

                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "The record was successfully updated");
                        idField.setText("");
                        nameField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, "No entry found with the specified ID");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Database connection error", "ERROR", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        updateFrame.add(inputPanel, BorderLayout.NORTH);
        updateFrame.add(buttonPanel, BorderLayout.SOUTH);
        updateFrame.setVisible(true);
    }

    //сохранении заказа как совершенного
    private void modifyOrdersData() {
        JFrame updateFrame = new JFrame("Change orders data");
        updateFrame.setSize(400, 200);
        updateFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        updateFrame.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        JTextField idField = new JTextField();
        JTextField service_idField = new JTextField();
        JTextField specialist_idField = new JTextField();
        JTextField request_dateField = new JTextField();
        JTextField apartmentField = new JTextField();

        inputPanel.add(new JLabel("Id:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Service_id:"));
        inputPanel.add(service_idField);
        inputPanel.add(new JLabel("Specialist_id:"));
        inputPanel.add(specialist_idField);
        inputPanel.add(new JLabel("Request_date:"));
        inputPanel.add(request_dateField);
        inputPanel.add(new JLabel("Apartment address:"));
        inputPanel.add(apartmentField);

        JButton completeButton = new JButton("Complete");
        completeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText().trim();
                String service_id = service_idField.getText().trim();
                String specialist_id = specialist_idField.getText().trim();
                String requestDate = request_dateField.getText().trim();
                String apartment_id = apartmentField.getText().trim();

                if (id.isEmpty() && service_id.isEmpty() && specialist_id.isEmpty() && requestDate.isEmpty() && apartment_id.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Fill in at least one field to complete", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                StringBuilder conditionBuilder = new StringBuilder();
                if (!id.isEmpty()) {
                    if (!id.matches("\\d+") || Integer.parseInt(id) <= 0) {
                        JOptionPane.showMessageDialog(null, "ID must be a positive integer", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    conditionBuilder.append("id=").append(Integer.valueOf(id)).append(" AND ");
                }
                if (!service_id.isEmpty()) {
                    if (!service_id.matches("\\d+") || Integer.parseInt(service_id) <= 0) {
                        JOptionPane.showMessageDialog(null, "Service ID must be a positive integer", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    conditionBuilder.append("service_id=").append(Integer.valueOf(service_id)).append(" AND ");
                }
                if (!specialist_id.isEmpty()) {
                    if (!specialist_id.matches("\\d+") || Integer.parseInt(specialist_id) <= 0) {
                        JOptionPane.showMessageDialog(null, "Specialist ID must be a positive integer", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    conditionBuilder.append("specialist_id=").append(Integer.valueOf(specialist_id)).append(" AND ");
                }
                if (!requestDate.isEmpty()) {
                    try {
                        Date.valueOf(requestDate);
                        conditionBuilder.append("request_date='").append(requestDate).append("' AND ");
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(null, "Request date must be in the format YYYY-MM-DD", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                if (!apartment_id.isEmpty()) {
                    if (!apartment_id.matches("\\d+") || Integer.parseInt(apartment_id) <= 0) {
                        JOptionPane.showMessageDialog(null, "Apartment ID must be a positive integer", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    conditionBuilder.append("apartment_id=").append(Integer.valueOf(apartment_id)).append(" AND ");
                }

                if (conditionBuilder.length() > 0) {
                    conditionBuilder.setLength(conditionBuilder.length() - 5); // Удаляем последний "AND"
                } else {
                    JOptionPane.showMessageDialog(null, "No valid parameters provided", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String selectQuery = "SELECT * FROM service_requests WHERE " + conditionBuilder;
                String deleteQuery = "DELETE FROM service_requests WHERE " + conditionBuilder;

                try {
                    try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
                         ResultSet resultSet = selectStatement.executeQuery()) {
                        if (resultSet.next()) {
                            int fetchedServiceId = resultSet.getInt("service_id");
                            int fetchedSpecialistId = resultSet.getInt("specialist_id");
                            Date fetchedRequestDate = resultSet.getDate("request_date");

                            String insertQuery = "INSERT INTO completed_orders (order_creation_date, order_completion_date, user_comment, service_id, specialist_id) VALUES (?, ?, ?, ?, ?)";
                            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                                insertStatement.setDate(1, fetchedRequestDate); // order_creation_date
                                insertStatement.setDate(2, new Date(System.currentTimeMillis())); // order_completion_date
                                insertStatement.setString(3, "No comments (release soon)"); // user_comment
                                insertStatement.setInt(4, fetchedServiceId); // service_id
                                insertStatement.setInt(5, fetchedSpecialistId); // specialist_id
                                insertStatement.executeUpdate();
                            }

                            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                                int rowsDeleted = deleteStatement.executeUpdate();
                                if (rowsDeleted > 0) {
                                    JOptionPane.showMessageDialog(null, "Order successfully completed!");
                                }
                            }
                            idField.setText("");
                            service_idField.setText("");
                            specialist_idField.setText("");
                            request_dateField.setText("");
                            apartmentField.setText("");
                        } else {
                            JOptionPane.showMessageDialog(null, "No matching order found to complete");
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error processing order completion");
                    ex.printStackTrace();
                }
            }
        });

        JButton deleteButton= new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id=idField.getText().trim();
                String service_id=service_idField.getText().trim();
                String specialist_id=specialist_idField.getText().trim();
                String requestDate=request_dateField.getText().trim();
                String apartment_id=apartmentField.getText().trim();

                if (id.isEmpty()&&service_id.isEmpty()&&specialist_id.isEmpty()&&requestDate.isEmpty()&&apartment_id.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Fill in at least one field to delete", "ERROR",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                StringBuilder queryBuilder = new StringBuilder("delete from service_requests where ");
                if (!id.isEmpty()){
                    if (!id.matches("\\d+") ||Integer.parseInt(id)<=0){
                        JOptionPane.showMessageDialog(null, "ID must be a positive integer", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    queryBuilder.append("id="+Integer.valueOf(id)+" and ");
                }
                if (!service_id.isEmpty()){
                    if (!service_id.matches("\\d+") || Integer.parseInt(service_id) <= 0) {
                        JOptionPane.showMessageDialog(null, "Service ID must be a positive integer", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    queryBuilder.append("service_id="+Integer.valueOf(service_id));
                }
                if(!specialist_id.isEmpty()){
                    if (!specialist_id.matches("\\d+") || Integer.parseInt(specialist_id) <= 0) {
                        JOptionPane.showMessageDialog(null, "Specialist ID must be a positive integer", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    queryBuilder.append("specialist_id="+Integer.valueOf(specialist_id)+" and ");
                }
                if(!requestDate.isEmpty()){
                    try {
                        Date.valueOf(requestDate);
                        queryBuilder.append("request_date='").append(requestDate).append("' AND ");

                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(null, "Request date must be in the format YYYY-MM-DD", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                if(!apartment_id.isEmpty()){
                    if (!apartment_id.matches("\\d+") || Integer.parseInt(apartment_id) <= 0) {
                        JOptionPane.showMessageDialog(null, "Apartment ID must be a positive integer", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    queryBuilder.append("apartment_id="+Integer.valueOf(apartment_id)+" and ");
                }
                queryBuilder.setLength(queryBuilder.length() - 4);

                try(PreparedStatement statement=connection.prepareStatement(queryBuilder.toString())){
                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "The record was successfully deleted from the warehouses table");
                        idField.setText("");
                        service_idField.setText("");
                        specialist_idField.setText("");
                        request_dateField.setText("");
                        apartmentField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, "An entry with the specified parameters was not found");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Database connection error");
                    ex.printStackTrace();
                }
            }
        });

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText().trim();
                String service_id = service_idField.getText().trim();
                String specialist_id = specialist_idField.getText().trim();
                String requestDate = request_dateField.getText().trim();
                String apartment_id = apartmentField.getText().trim();

                if (id.isEmpty() || !id.matches("\\d+") || Integer.parseInt(id) <= 0) {
                    JOptionPane.showMessageDialog(null, "Enter a valid positive ID for the modification", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (service_id.isEmpty() && specialist_id.isEmpty() && requestDate.isEmpty() && apartment_id.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Fill in at least one field to modify", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                StringBuilder queryBuilder = new StringBuilder("UPDATE service_requests SET ");
                boolean hasFields = false;

                if (!specialist_id.isEmpty()) {
                    if (!specialist_id.matches("\\d+") || Integer.parseInt(specialist_id) <= 0) {
                        JOptionPane.showMessageDialog(null, "Specialist ID must be a positive integer", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    queryBuilder.append("specialist_id = ?, ");
                    hasFields = true;
                }
                if (!requestDate.isEmpty()) {
                    try {
                        Date.valueOf(requestDate); // Проверка валидности даты
                        queryBuilder.append("request_date = ?, ");
                        hasFields = true;
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(null, "Request date must be in the format YYYY-MM-DD", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                if (!apartment_id.isEmpty()) {
                    if (!apartment_id.matches("\\d+") || Integer.parseInt(apartment_id) <= 0) {
                        JOptionPane.showMessageDialog(null, "Apartment ID must be a positive integer", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    queryBuilder.append("apartment_id = ?, ");
                    hasFields = true;
                }
                if (!hasFields) {
                    JOptionPane.showMessageDialog(null, "No valid fields to update", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                queryBuilder.setLength(queryBuilder.length() - 2);
                queryBuilder.append(" WHERE id = ?");

                try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
                    int paramIndex = 1;

                    if (!specialist_id.isEmpty()) {
                        statement.setInt(paramIndex++, Integer.parseInt(specialist_id));
                    }
                    if (!requestDate.isEmpty()) {
                        statement.setDate(paramIndex++, Date.valueOf(requestDate));
                    }
                    if (!apartment_id.isEmpty()) {
                        statement.setInt(paramIndex++, Integer.parseInt(apartment_id));
                    }

                    statement.setInt(paramIndex, Integer.parseInt(id));

                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "The record was successfully updated");
                        idField.setText("");
                        service_idField.setText("");
                        specialist_idField.setText("");
                        request_dateField.setText("");
                        apartmentField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, "No entry found with the specified ID");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Database connection error", "ERROR", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(completeButton);
        updateFrame.add(inputPanel, BorderLayout.NORTH);
        updateFrame.add(buttonPanel, BorderLayout.SOUTH);
        updateFrame.setVisible(true);
    }

    //главное меню пользователя
    private JPanel mainUserPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(30, 50, 120));

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                GradientPaint gradient = new GradientPaint(0, 0, new Color(255, 0, 136),
                        getWidth(), getHeight(), new Color(65, 0, 133));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                int dotSize = 4;
                int spacing = 2;
                for (int x = 0; x < getWidth(); x += spacing) {
                    for (int y = 0; y < getHeight(); y += spacing) {
                        g2d.setColor(new Color(249, 168, 255, 60));
                        g2d.fillOval(x, y, dotSize, dotSize);
                    }
                }

                GradientPaint fade = new GradientPaint(0, 0, new Color(0, 0, 0, 0),
                        0, getHeight(), new Color(0, 22, 87));
                g2d.setPaint(fade);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.dispose();
            }
        };
        backgroundPanel.setLayout(null);

        JLabel titleLabel = new JLabel("Choose an option that you prefer!", JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                String text = getText();
                FontMetrics fm = g2d.getFontMetrics(getFont());

                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;

                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.drawString(text, x + 2, y + 2);

                g2d.setColor(Color.BLACK);
                g2d.drawString(text, x - 1, y - 1);
                g2d.drawString(text, x + 1, y - 1);
                g2d.drawString(text, x - 1, y + 1);
                g2d.drawString(text, x + 1, y + 1);

                g2d.setColor(Color.WHITE);
                g2d.drawString(text, x, y);

                g2d.dispose();
            }
        };
        titleLabel.setFont(new Font("JetBrains Mono", Font.BOLD | Font.ITALIC, 35));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(false);
        titleLabel.setBounds(0, 35, 800, 65);
        backgroundPanel.add(titleLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(50, 120, 500, 400);

        buttonPanel.add(Box.createVerticalGlue());

        buttonPanel.add(createStyledButton("View the list of services", e -> viewData("Services", "select * from services ORDER BY id ASC")));
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(createStyledButton("View the list of specialists", e -> viewData("Specialists", "select * from specialists ORDER BY id ASC")));
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(createStyledButton("Unfulfilled orders", e ->
                viewData("Not completed", "select service_id as \"Service id\", specialist_id as \"Specialist id\", request_date as \"Request date\" from service_requests")));
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(createStyledButton("Completed orders", e ->
                viewData("Completed",
                        "SELECT service_id AS \"Service id\", specialist_id AS \"Specialist id\", " +
                                "order_creation_date AS \"Request date\", order_completion_date AS \"Completion date\" " +
                                "FROM completed_orders;")));
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(createStyledButton("Order a new service", e -> orderNewService()));

        buttonPanel.add(Box.createVerticalGlue());

        backgroundPanel.add(buttonPanel);
        mainPanel.add(backgroundPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JButton createStyledButton(String text, ActionListener action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();

                GradientPaint gradient = new GradientPaint(0, 0, new Color(255, 138, 131),
                        getWidth(), getHeight(), new Color(132, 60, 180));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 30, 30);

                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fillRoundRect(4, 4, getWidth() - 4, getHeight() - 4, 30, 30);

                super.paintComponent(g2d);
                g2d.dispose();
            }
        };

        button.setFont(new Font("JetBrains Mono", Font.PLAIN, 16));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);
        /*
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                animateHoverEffect(button, 1.1f);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                animateHoverEffect(button, 1.0f);
            }
        });

        button.addActionListener(e -> animateClickEffect(button));
        */
        return button;
    }

    private void animateHoverEffect(JButton button, float scale) {
        Dimension originalSize = button.getPreferredSize();

        Timer timer = new Timer(10, null);
        long startTime = System.currentTimeMillis();
        int duration = 10;
        Dimension targetSize = new Dimension(
                (int) (originalSize.width * scale),
                (int) (originalSize.height * scale)
        );

        timer.addActionListener(e -> {
            float progress = (float) (System.currentTimeMillis() - startTime) / duration;
            if (progress >= 1.0f) {
                progress = 1.0f;
                timer.stop();
            }

            int width = (int) (originalSize.width + (targetSize.width - originalSize.width) * progress);
            int height = (int) (originalSize.height + (targetSize.height - originalSize.height) * progress);

            button.setPreferredSize(new Dimension(width, height));
            button.revalidate();
            button.repaint();
        });

        timer.start();

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                button.setPreferredSize(originalSize);
                button.revalidate();
                button.repaint();
            }
        });
    }

    private void animateClickEffect(JButton button) {
        Timer shrinkTimer = new Timer(10, null);
        long startTime = System.currentTimeMillis();
        int duration = 100;
        Dimension originalSize = button.getPreferredSize();
        Dimension smallerSize = new Dimension(
                originalSize.width - 10,
                originalSize.height - 5
        );

        shrinkTimer.addActionListener(e -> {
            float progress = (float) (System.currentTimeMillis() - startTime) / duration;
            if (progress >= 1.0f) {
                progress = 1.0f;
                shrinkTimer.stop();

                Timer restoreTimer = new Timer(10, null);
                long restoreStartTime = System.currentTimeMillis();
                restoreTimer.addActionListener(ev -> {
                    float restoreProgress = (float) (System.currentTimeMillis() - restoreStartTime) / duration;
                    if (restoreProgress >= 1.0f) {
                        restoreProgress = 1.0f;
                        restoreTimer.stop();
                    }

                    int width = (int) (smallerSize.width + (originalSize.width - smallerSize.width) * restoreProgress);
                    int height = (int) (smallerSize.height + (originalSize.height - smallerSize.height) * restoreProgress);

                    button.setPreferredSize(new Dimension(width, height));
                    button.revalidate();
                    button.repaint();
                });
                restoreTimer.start();
            }

            int width = (int) (originalSize.width + (smallerSize.width - originalSize.width) * progress);
            int height = (int) (originalSize.height + (smallerSize.height - originalSize.height) * progress);

            button.setPreferredSize(new Dimension(width, height));
            button.revalidate();
            button.repaint();
        });

        shrinkTimer.start();
    }

    private void orderNewService() {
        JFrame insertOrderFrame = new JFrame("Make an order");
        insertOrderFrame.setSize(800, 400);
        insertOrderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        insertOrderFrame.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel(new GridLayout(5, 1, 10, 10));

        JTextField serviceIdField = new JTextField();
        JTextField specialistIdField = new JTextField();
        JTextField streetField = new JTextField();
        JTextField houseField = new JTextField();
        JTextField apartmentNumberField = new JTextField();

        inputPanel.add(new JLabel("Id of service:"));
        inputPanel.add(serviceIdField);
        inputPanel.add(new JLabel("Id of specialist (if you want a specific employee to fulfill your order):"));
        inputPanel.add(specialistIdField);
        inputPanel.add(new JLabel("Street:"));
        inputPanel.add(streetField);
        inputPanel.add(new JLabel("House number:"));
        inputPanel.add(houseField);
        inputPanel.add(new JLabel("Apartment number (if you don't have an own house):"));
        inputPanel.add(apartmentNumberField);

        JButton addButton = new JButton("Make an order");
        addButton.addActionListener(e -> {
            String serviceId = serviceIdField.getText().trim();
            String specialistId = specialistIdField.getText().trim();
            String street = streetField.getText().trim();
            String house = houseField.getText().trim();
            String apartmentNumber = apartmentNumberField.getText().trim();
            if (serviceId.isEmpty() || street.isEmpty() || house.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Fill at least in service, street, and house fields");
                return;
            }
            String apartmentAddress = street + ", " + house + ", " + apartmentNumber;

            try {
                connection.setAutoCommit(false);
                int apartmentId = -1;
                try (PreparedStatement checkStatement = connection.prepareStatement(
                        "SELECT id FROM apartments WHERE address = ?")) {
                    checkStatement.setString(1, apartmentAddress);
                    try (ResultSet rs = checkStatement.executeQuery()) {
                        if (rs.next()) {
                            apartmentId = rs.getInt("id");
                        }
                    }
                }

                if (apartmentId == -1) {
                    int confirm = JOptionPane.showConfirmDialog(
                            null,
                            "Apartment address not found. Do you want to add it?",
                            "Confirm Address",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        try (PreparedStatement insertApartmentStatement = connection.prepareStatement(
                                "INSERT INTO apartments (address) VALUES (?)",
                                Statement.RETURN_GENERATED_KEYS)) {
                            insertApartmentStatement.setString(1, apartmentAddress);
                            insertApartmentStatement.executeUpdate();

                            try (ResultSet generatedKeys = insertApartmentStatement.getGeneratedKeys()) {
                                if (generatedKeys.next()) {
                                    apartmentId = generatedKeys.getInt(1);
                                }
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Order canceled");
                        connection.rollback();
                        return;
                    }
                }

                if (!specialistId.isEmpty()) {
                    int specId = Integer.parseInt(specialistId);
                    int serviceIdInt = Integer.parseInt(serviceId);
                    boolean isServiceSupported = false;
                    try (PreparedStatement checkSpecialistStatement = connection.prepareStatement(
                            "SELECT 1 FROM specialists WHERE id = ? AND ? = ANY (service_id)")) {
                        checkSpecialistStatement.setInt(1, specId);
                        checkSpecialistStatement.setInt(2, serviceIdInt);

                        try (ResultSet rs = checkSpecialistStatement.executeQuery()) {
                            if (rs.next()) {
                                isServiceSupported = true;
                            }
                        }
                    }

                    if (!isServiceSupported) {
                        JOptionPane.showMessageDialog(null, "The selected specialist cannot perform the specified service.");
                        connection.rollback();
                        return;
                    }
                }

                String query = "INSERT INTO service_requests (service_id, apartment_id";
                if (!specialistId.isEmpty()) {
                    query += ", specialist_id";
                }
                query += ") VALUES (?, ?";
                if (!specialistId.isEmpty()) {
                    query += ", ?";
                }
                query += ")";

                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setInt(1, Integer.parseInt(serviceId));
                    statement.setInt(2, apartmentId);

                    if (!specialistId.isEmpty()) {
                        statement.setInt(3, Integer.parseInt(specialistId));
                    }

                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "The order has been successfully placed");

                        serviceIdField.setText("");
                        specialistIdField.setText("");
                        streetField.setText("");
                        houseField.setText("");
                        apartmentNumberField.setText("");
                        connection.commit();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error when placing the order");
                        connection.rollback();
                    }
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Database connection error");
                ex.printStackTrace();
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "The Service ID and Specialist ID fields must contain numeric values");
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        insertOrderFrame.add(inputPanel, BorderLayout.NORTH);
        insertOrderFrame.add(buttonPanel, BorderLayout.SOUTH);
        insertOrderFrame.setVisible(true);
    }

    //вывод данных таблицы бд
    private void viewData(String outName, String query) {
        JFrame infoFrame = new JFrame(outName);
        infoFrame.setSize(600, 400);
        infoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        infoFrame.setLocationRelativeTo(this);

        DefaultTableModel tableModel = new DefaultTableModel();
        JTable warehousesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(warehousesTable);
        infoFrame.add(scrollPane, BorderLayout.CENTER);

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            tableModel.setRowCount(0);
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnName(i));
            }

            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = resultSet.getObject(i);
                }
                tableModel.addRow(rowData);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving data: " + e.getMessage());
        }

        infoFrame.setVisible(true);
    }
}
