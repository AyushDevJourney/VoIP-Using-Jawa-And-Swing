
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

// 🎧 Enhanced for a futuristic, cyberpunk aesthetic to match the Sender
public class AudioReceiver extends JFrame {
    private JTextField portField;
    private JButton startButton, stopButton;
    private JTextArea logArea;
    private final AtomicBoolean receiving = new AtomicBoolean(false);
    // ⭐ FIX: Hold a reference to the DatagramSocket for safe closing/unblocking
    private DatagramSocket socket = null; 

    // Color Palette (MUST MATCH Sender)
    private static final Color DARK_BG = new Color(15, 15, 25); // Deep Blue/Black
    private static final Color MEDIUM_DARK_BG = new Color(25, 25, 40); // Slightly lighter for panels
    private static final Color NEON_ACCENT = new Color(0, 255, 255); // Electric Cyan for accents
    private static final Color NEON_TEXT = new Color(200, 255, 200); // Pale Neon Green for log text
    private static final Color INPUT_BG = new Color(50, 50, 70); // Dark Blue for input fields
    private static final Color CONTROL_BG = new Color(35, 35, 50); // Dark background for controls

    public AudioReceiver() {
        setTitle("🔊 IoHT AUDIO RECEIVER UNIT");
        setSize(550, 400); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(DARK_BG);

        // 🚨 Main Panel setup
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(DARK_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(mainPanel, BorderLayout.CENTER);

        // ## 1. Title Panel
        JLabel title = new JLabel("📡 REALTIME AUDIO RECEIVER PROTOCOL", SwingConstants.CENTER);
        title.setForeground(NEON_ACCENT);
        title.setFont(new Font("Consolas", Font.BOLD, 24));
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, NEON_ACCENT.darker()));
        mainPanel.add(title, BorderLayout.NORTH);

        // ## 2. Input/Controls Panel (Center)
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        centerPanel.setBackground(DARK_BG);

        // 2a. Port Input Panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        inputPanel.setBackground(MEDIUM_DARK_BG);
        inputPanel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NEON_ACCENT.darker().darker(), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            )
        );

        JLabel portLabel = createLabel("MONITOR PORT:");
        portField = createTextField("50005");
        portField.setColumns(8);

        inputPanel.add(portLabel);
        inputPanel.add(portField);
        centerPanel.add(inputPanel);

        // 2b. Controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        controlPanel.setBackground(CONTROL_BG);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        startButton = createActionButton("▶ START LISTENING");
        stopButton = createActionButton("⏹ STOP LISTENING");
        stopButton.setEnabled(false);

        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        centerPanel.add(controlPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ## 3. Log Area (Terminal Style)
        logArea = new JTextArea("SYSTEM: Ready to receive...\nSTATUS: Awaiting Command\n");
        logArea.setBackground(DARK_BG.darker());
        logArea.setForeground(NEON_TEXT);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setEditable(false);
        logArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(500, 150));
        scrollPane.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(NEON_ACCENT, 1),
                "RECEIVER LOG",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Consolas", Font.BOLD, 10),
                NEON_ACCENT
            )
        );
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        // ## 4. Button Listeners
        startButton.addActionListener(e -> startReceiving());
        stopButton.addActionListener(e -> stopReceiving());

        setLocationRelativeTo(null); 
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
        field.setCaretColor(NEON_ACCENT); 
        field.setBorder(BorderFactory.createLineBorder(NEON_ACCENT.darker(), 1));
        return field;
    }
    
    // Helper to create action buttons with hover effect
    private JButton createActionButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Consolas", Font.BOLD, 12));
        btn.setBackground(NEON_ACCENT.darker()); 
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createRaisedBevelBorder());
        btn.setMargin(new Insets(10, 20, 10, 20));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                if (btn.isEnabled()) {
                    btn.setBackground(NEON_ACCENT); 
                }
            }
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(NEON_ACCENT.darker()); 
            }
        });
        
        return btn;
    }

    // --- Core Functionality with Fixes ---

    private void startReceiving() {
        receiving.set(true);
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        log("🎧 PROTOCOL ACTIVE: Monitoring port " + portField.getText().trim() + " for incoming audio data.");

        new Thread(() -> {
            DatagramSocket currentSocket = null;
            SourceDataLine speakers = null;

            try {
                int port = Integer.parseInt(portField.getText().trim());
                currentSocket = new DatagramSocket(port);
                socket = currentSocket; // Store socket reference for safe closing
                currentSocket.setSoTimeout(0); 

                AudioFormat format = new AudioFormat(8000f, 16, 1, true, false);
                speakers = AudioSystem.getSourceDataLine(format);
                speakers.open(format, 3200);
                speakers.start();
                log("🔊 Audio line established. Ready for data stream.");

                byte[] ulawBuffer = new byte[320];
                byte[] pcmBuffer = new byte[640];
                DatagramPacket packet = new DatagramPacket(ulawBuffer, ulawBuffer.length);

                long nextPlay = System.nanoTime();
                long frameTime = 40_000_000L; // 40ms

                while (receiving.get()) {
                    currentSocket.receive(packet); // Blocking call

                    // u-Law decoding
                    for (int i = 0, j = 0; i < ulawBuffer.length; i++, j += 2) {
                        short s = uLawToLinear(ulawBuffer[i]);
                        pcmBuffer[j] = (byte) s;
                        pcmBuffer[j + 1] = (byte) (s >> 8);
                    }

                    // Simple timing synchronization
                    long now = System.nanoTime();
                    if (now < nextPlay)
                        Thread.sleep((nextPlay - now) / 1_000_000);
                    nextPlay += frameTime;

                    speakers.write(pcmBuffer, 0, pcmBuffer.length);
                }

                log("🛑 RECEIVER STOPPED gracefully.");

            } catch (SocketException e) {
                // This is expected when the socket is closed externally via stopReceiving()
                if (receiving.get()) {
                    log("❌ Socket Error (unexpected): " + e.getMessage());
                } else {
                    log("✅ Shutdown complete.");
                }
            } catch (Exception e) {
                log("❌ CRITICAL ERROR: " + e.getMessage());
            } finally {
                // ⭐ FIX: Ensure all resources are cleaned up and buttons are reset
                if (speakers != null) {
                    speakers.drain();
                    speakers.close();
                }
                if (currentSocket != null && !currentSocket.isClosed()) {
                    currentSocket.close();
                }
                // Reset the global socket reference
                socket = null;
                receiving.set(false);
                
                SwingUtilities.invokeLater(() -> {
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                });
            }
        }).start();
    }

    private void stopReceiving() {
        log("🛑 INITIATING SHUTDOWN SEQUENCE...");
        receiving.set(false);
        // ⭐ FIX: Unblock the DatagramSocket.receive() call immediately by closing the socket
        if (socket != null) {
            SwingUtilities.invokeLater(() -> {
                // Must be run in a separate runnable to avoid deadlock if called from the AWT thread,
                // but since stopReceiving() is called from an ActionListener, this is safe.
                socket.close();
            });
        } else {
             // If socket is null, the thread probably hasn't started or already finished.
             log("STATUS: Socket not active or already closed.");
             startButton.setEnabled(true);
             stopButton.setEnabled(false);
        }
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> logArea.append(msg + "\n"));
    }

    // µ-Law decoder (unchanged)
    private static final int BIAS = 0x84;
    private static short uLawToLinear(byte ulaw) {
        ulaw = (byte) ~ulaw;
        int sign = ulaw & 0x80;
        int exponent = (ulaw & 0x70) >> 4;
        int mantissa = ulaw & 0x0F;
        int sample = (((mantissa << 4) + 0x08) << (exponent + 3)) - BIAS;
        return (short) ((sign != 0) ? -sample : sample);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { /* Ignore L&F change failure */ }
        
        SwingUtilities.invokeLater(AudioReceiver::new);
    }
}