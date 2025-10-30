import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

// 🚀 Enhanced for a futuristic, cyberpunk aesthetic
public class AudioSenderUI extends JFrame {
    private JTextField ipField, portField;
    private JButton startButton, stopButton;
    private JTextArea logArea;
    private final AtomicBoolean sending = new AtomicBoolean(false);

    // Color Palette
    private static final Color DARK_BG = new Color(15, 15, 25); // Deep Blue/Black
    private static final Color MEDIUM_DARK_BG = new Color(25, 25, 40); // Slightly lighter for panels
    private static final Color NEON_ACCENT = new Color(0, 255, 255); // Electric Cyan for accents
    private static final Color NEON_TEXT = new Color(200, 255, 200); // Pale Neon Green for log text
    private static final Color INPUT_BG = new Color(50, 50, 70); // Dark Blue for input fields

    public AudioSenderUI() {
        setTitle("🎤 IoHT AUDIO COMM-LINK");
        setSize(600, 450); // Increased size for better layout
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(DARK_BG);
        
        // 🚨 Main Panel setup
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(DARK_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(mainPanel, BorderLayout.CENTER);

        // ## 1. Title Panel
        JLabel title = new JLabel("🎙 REALTIME AUDIO STREAM PROTOCOL", SwingConstants.CENTER);
        title.setForeground(NEON_ACCENT);
        title.setFont(new Font("Consolas", Font.BOLD, 24));
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, NEON_ACCENT.darker())); // Underline effect
        mainPanel.add(title, BorderLayout.NORTH);

        // ## 2. Input and Controls Panel (Central)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        centerPanel.setBackground(DARK_BG);

        // 2a. Input/Parameter Panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(MEDIUM_DARK_BG);
        inputPanel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NEON_ACCENT.darker().darker(), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            )
        );
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Receiver IP
        JLabel ipLabel = createLabel("TARGET IP ADDRESS:");
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3; inputPanel.add(ipLabel, gbc);
        
        ipField = createTextField("127.0.0.1");
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7; inputPanel.add(ipField, gbc);
        
        // Port
        JLabel portLabel = createLabel("COMM-PORT:");
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3; inputPanel.add(portLabel, gbc);

        portField = createTextField("50005");
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7; inputPanel.add(portField, gbc);

        // Controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(MEDIUM_DARK_BG);
        
        startButton = createActionButton("▶ ACTIVATE STREAM");
        stopButton = createActionButton("⏹ TERMINATE STREAM");
        stopButton.setEnabled(false);
        
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weighty = 1.0; gbc.anchor = GridBagConstraints.SOUTH;
        inputPanel.add(controlPanel, gbc);
        
        centerPanel.add(inputPanel);

        // 2b. Log Area (Terminal Style)
        logArea = new JTextArea("SYSTEM: Initializing...\nSTATUS: Idle\n");
        logArea.setBackground(DARK_BG.darker());
        logArea.setForeground(NEON_TEXT);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(NEON_ACCENT, 1),
                "SYSTEM LOG", 
                javax.swing.border.TitledBorder.LEFT, 
                javax.swing.border.TitledBorder.TOP, 
                new Font("Consolas", Font.BOLD, 10), 
                NEON_ACCENT
            )
        );
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        centerPanel.add(scrollPane);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ## 3. Button Listeners (kept for functionality)
        startButton.addActionListener(e -> startSending());
        stopButton.addActionListener(e -> stopSending());

        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }
    
    // Helper to create futuristic labels
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(NEON_ACCENT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return label;
    }
    
    // Helper to create futuristic text fields
    private JTextField createTextField(String defaultText) {
        JTextField field = new JTextField(defaultText);
        field.setFont(new Font("Consolas", Font.PLAIN, 14));
        field.setBackground(INPUT_BG);
        field.setForeground(Color.WHITE);
        field.setCaretColor(NEON_ACCENT); // Futuristic cursor
        field.setBorder(BorderFactory.createLineBorder(NEON_ACCENT.darker(), 1));
        return field;
    }
    
    // Helper to create action buttons with hover effect
    private JButton createActionButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Consolas", Font.BOLD, 12));
        btn.setBackground(NEON_ACCENT.darker()); // Darker default
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createRaisedBevelBorder());
        btn.setMargin(new Insets(10, 20, 10, 20));
        
        // Add a simple hover effect for polish
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                if (btn.isEnabled()) {
                    btn.setBackground(NEON_ACCENT); // Brighten on hover
                }
            }
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(NEON_ACCENT.darker()); // Back to dark on exit
            }
        });
        
        return btn;
    }

    // --- Core Functionality (No change needed) ---

    private void startSending() {
        sending.set(true);
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        log("🎤 PROTOCOL ACTIVE: Streaming to " + ipField.getText().trim() + ":" + portField.getText().trim());

        new Thread(() -> {
            try {
                InetAddress targetIP = InetAddress.getByName(ipField.getText().trim());
                int port = Integer.parseInt(portField.getText().trim());
                DatagramSocket socket = new DatagramSocket();

                AudioFormat format = new AudioFormat(8000f, 16, 1, true, false);
                TargetDataLine mic = AudioSystem.getTargetDataLine(format);
                mic.open(format);
                mic.start();

                byte[] buffer = new byte[640];
                byte[] ulawBuffer = new byte[320];

                while (sending.get()) {
                    int bytesRead = mic.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        for (int i = 0, j = 0; i < ulawBuffer.length; i++, j += 2) {
                            short sample = (short) ((buffer[j + 1] << 8) | (buffer[j] & 0xFF));
                            ulawBuffer[i] = linearToULaw(sample);
                        }
                        socket.send(new DatagramPacket(ulawBuffer, ulawBuffer.length, targetIP, port));
                    }
                }

                mic.stop();
                mic.close();
                socket.close();
                log("⏹ TRANSMISSION ENDED. Resources de-allocated.");
            } catch (Exception ex) {
                log("❌ CRITICAL ERROR: " + ex.getMessage());
                SwingUtilities.invokeLater(() -> {
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    sending.set(false);
                });
            }
        }).start();
    }

    private void stopSending() {
        sending.set(false);
        // Buttons will be re-enabled/disabled in the thread's catch/finally block
        log("🛑 INITIATING TERMINATION SEQUENCE...");
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> logArea.append(msg + "\n"));
    }

    // µ-Law encoder (unchanged)
    private static final int BIAS = 0x84, CLIP = 32635;
    private static byte linearToULaw(short sample) {
        int sign = (sample >> 8) & 0x80;
        if (sign != 0) sample = (short) -sample;
        if (sample > CLIP) sample = CLIP;
        sample += BIAS;

        int exponent = 7;
        for (int expMask = 0x4000; (sample & expMask) == 0 && exponent > 0; exponent--, expMask >>= 1);
        int mantissa = (sample >> ((exponent == 0) ? 4 : (exponent + 3))) & 0x0F;
        return (byte) ~(sign | (
exponent << 4) | mantissa);
    }

    public static void main(String[] args) {
        // Set a modern look and feel if available (e.g., Nimbus, but metal is default)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { /* Ignore L&F change failure */ }
        
        SwingUtilities.invokeLater(AudioSenderUI::new);
    }
}