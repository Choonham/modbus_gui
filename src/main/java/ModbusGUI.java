import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;

public class ModbusGUI extends JFrame {
    private JTextArea textArea;
    private InetAddress host = null;
    private int port = Modbus.DEFAULT_PORT;
    int slaveId = 1;  // Replace with the appropriate slave ID

    private JTextField referenceField;
    private JTextField wordCountField;

    public ModbusGUI() {
        // Set up the frame
        setTitle("Modbus GUI Example");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        referenceField = new JTextField(10);
        wordCountField = new JTextField(10);

        // Create the text area
        textArea = new JTextArea();
        textArea.setEditable(false);

        // Create the button
        JButton button = new JButton("Read Modbus Signal");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Read the Modbus signal
                String signal = readMODbusSignal();

                // Display the signal in the text area
                textArea.append(signal + "\n");
            }
        });

        // Add components to the frame
        JPanel centerPane = new JPanel(new GridLayout(2, 2, 2, 2));
        centerPane.add(new JLabel("Reference:"));
        centerPane.add(referenceField);
        centerPane.add(new JLabel("Word Count:"));
        centerPane.add(wordCountField);

        Container borderPane = getContentPane();
        borderPane.setLayout(new BorderLayout());
        borderPane.add(button, BorderLayout.NORTH);
        borderPane.add(centerPane, BorderLayout.CENTER);
        borderPane.add(textArea, BorderLayout.SOUTH);
    }

    private String readMODbusSignal() {

        try {
            // Establish a connection to the Modbus device
            host = InetAddress.getByName("127.0.0.1");

            TCPMasterConnection connection = new TCPMasterConnection(host);
            connection.setPort(port);
            connection.connect(); // Read the reference and word count from the text fields

            int reference = Integer.parseInt(referenceField.getText());
            int wordCount = Integer.parseInt(wordCountField.getText());
            // Prepare the request to read input registers
            ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest(reference, wordCount);
            request.setUnitID(slaveId);

            // Prepare the transaction
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
            transaction.setRequest(request);
            // Execute the transaction
            transaction.execute();

            // Get the response
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse) transaction.getResponse();

            // Extract the value from the response
            int value = response.getRegisterValue(0);  // Replace with the appropriate register index

            for (int i = 0; i < wordCount; i++) {
                int temp = response.getRegisterValue(i);
                System.out.println("Holding Register " + (i) + ": " + temp);
            }

            connection.close();

            return "MODbus signal value: " + value;

        } catch (Exception e) {
            e.printStackTrace();
            return "MODbus signal error: " + e;
        }
    }


    public static void main(String[] args) {
        // Create and show the GUI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ModbusGUI gui = new ModbusGUI();
                gui.setVisible(true);
            }
        });
    }
}
