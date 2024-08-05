import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class coaba extends JFrame {

    private DefaultListModel<String> taskListModel;
    private JList<String> taskList;
    private JTextField taskInputField;
    private JButton addButton;
    private JButton removeButton;
    private JButton manageFileButton;
    private JButton exportButton;
    private File currentFile; // Variable to keep track of the current file
    private int editingIndex = -1; // To keep track of the item being edited

    public coaba() {
        setTitle("To-Do List App");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        JScrollPane taskScrollPane = new JScrollPane(taskList);

        taskInputField = new JTextField(20);
        addButton = new JButton("Add/Update Task");
        removeButton = new JButton("Remove Task");
        manageFileButton = new JButton("Load/Update File");
        exportButton = new JButton("Export");

        JPanel inputPanel = new JPanel();
        inputPanel.add(taskInputField);
        inputPanel.add(addButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(removeButton);
        buttonPanel.add(manageFileButton);
        buttonPanel.add(exportButton);

        add(taskScrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String task = taskInputField.getText();
                if (task.isEmpty()) {
                    JOptionPane.showMessageDialog(coaba.this, "Input field is empty. Please enter a task.");
                    return;
                }

                if (editingIndex == -1) {
                    // If no task is being edited, add a new task
                    taskListModel.addElement(task);
                } else {
                    // If a task is being edited, update the existing task
                    taskListModel.set(editingIndex, task);
                    editingIndex = -1; // Reset editing index
                }
                
                taskInputField.setText(""); // Clear the input field after adding/updating
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = taskList.getSelectedIndex();
                if (selectedIndex != -1) {
                    // Reset editing index if a task is removed
                    if (selectedIndex == editingIndex) {
                        editingIndex = -1;
                    }
                    taskListModel.remove(selectedIndex);
                }
            }
        });

        manageFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentFile == null) {
                    // If no file has been loaded, prompt for a file to load
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Select a file to load");

                    int userSelection = fileChooser.showOpenDialog(coaba.this);
                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        currentFile = fileChooser.getSelectedFile();
                        try (BufferedReader reader = new BufferedReader(new FileReader(currentFile))) {
                            taskListModel.clear(); // Clear existing tasks
                            String line;
                            while ((line = reader.readLine()) != null) {
                                taskListModel.addElement(line);
                            }
                            JOptionPane.showMessageDialog(coaba.this, "Tasks loaded from " + currentFile.getAbsolutePath() + " successfully!");
                        } catch (IOException ioException) {
                            JOptionPane.showMessageDialog(coaba.this, "Error loading tasks: " + ioException.getMessage());
                        }
                    }
                } else {
                    // If a file has been loaded, update the file
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
                        for (int i = 0; i < taskListModel.size(); i++) {
                            writer.write(taskListModel.get(i));
                            writer.newLine();
                        }
                        JOptionPane.showMessageDialog(coaba.this, "Tasks updated in " + currentFile.getAbsolutePath() + " successfully!");
                    } catch (IOException ioException) {
                        JOptionPane.showMessageDialog(coaba.this, "Error updating tasks: " + ioException.getMessage());
                    }
                }
            }
        });

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentFile == null) {
                    // If no file has been loaded, prompt for a file to save
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Specify a file to save");

                    int userSelection = fileChooser.showSaveDialog(coaba.this);
                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        currentFile = fileChooser.getSelectedFile();

                        // Add .txt extension if not present
                        String filePath = currentFile.getAbsolutePath();
                        if (!filePath.toLowerCase().endsWith(".txt")) {
                            currentFile = new File(filePath + ".txt");
                        }
                    } else {
                        return; // User canceled file selection
                    }
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
                    for (int i = 0; i < taskListModel.size(); i++) {
                        writer.write(taskListModel.get(i));
                        writer.newLine();
                    }
                    JOptionPane.showMessageDialog(coaba.this, "Tasks exported to " + currentFile.getAbsolutePath() + " successfully!");
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(coaba.this, "Error exporting tasks: " + ioException.getMessage());
                }
            }
        });

        // Add a listener to handle selection in the list
        taskList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = taskList.getSelectedIndex();
                if (selectedIndex != -1) {
                    taskInputField.setText(taskListModel.getElementAt(selectedIndex));
                    editingIndex = selectedIndex; // Set the index of the task being edited
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new coaba().setVisible(true);
            }
        });
    }
}
