package com.peach.JumpToOtherEditer.settings;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.peach.JumpToOtherEditer.model.EditorConfig;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AppSettingsComponent {

    private final JPanel myMainPanel;
    private final JBTable editorsTable;
    private final EditorTableModel tableModel;
    private List<EditorConfig> editingEditors;

    public AppSettingsComponent() {
        myMainPanel = new JPanel(new BorderLayout());
        myMainPanel.setBackground(UIUtil.getPanelBackground());
        myMainPanel.setBorder(JBUI.Borders.empty(10));

        // Initialize with current settings
        editingEditors = AppSettingsState.getInstance().copyEditors();
        tableModel = new EditorTableModel(editingEditors);
        editorsTable = new JBTable(tableModel);

        setupTable();

        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setBackground(UIUtil.getPanelBackground());

        // Add tips panel at top
        contentPanel.add(createTipsPanel(), BorderLayout.NORTH);

        // Add table with toolbar
        contentPanel.add(createTablePanel(), BorderLayout.CENTER);

        myMainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private void setupTable() {
        editorsTable.setRowHeight(30);
        editorsTable.setShowGrid(true);
        editorsTable.setGridColor(JBColor.border());
        editorsTable.setIntercellSpacing(new Dimension(1, 1));
        editorsTable.setFillsViewportHeight(true);
        editorsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Column widths
        TableColumn enabledCol = editorsTable.getColumnModel().getColumn(0);
        enabledCol.setPreferredWidth(60);
        enabledCol.setMaxWidth(80);
        enabledCol.setMinWidth(50);

        TableColumn nameCol = editorsTable.getColumnModel().getColumn(1);
        nameCol.setPreferredWidth(150);

        TableColumn commandCol = editorsTable.getColumnModel().getColumn(2);
        commandCol.setPreferredWidth(120);

        TableColumn pathCol = editorsTable.getColumnModel().getColumn(3);
        pathCol.setPreferredWidth(350);

        TableColumn builtinCol = editorsTable.getColumnModel().getColumn(4);
        builtinCol.setPreferredWidth(70);
        builtinCol.setMaxWidth(80);
        builtinCol.setMinWidth(60);

        // Custom renderer for path column with browse button hint
        pathCol.setCellRenderer(new PathCellRenderer());
        pathCol.setCellEditor(new PathCellEditor());

        // Custom renderer for builtin column
        builtinCol.setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setText((Boolean) value ? "✓" : "");
                return this;
            }
        });
    }

    private JPanel createTipsPanel() {
        JPanel tipsPanel = new JPanel();
        tipsPanel.setLayout(new BoxLayout(tipsPanel, BoxLayout.Y_AXIS));
        tipsPanel.setBorder(JBUI.Borders.empty(10));
        tipsPanel.setBackground(new JBColor(new Color(255, 248, 225), new Color(50, 45, 30)));

        // Title
        JBLabel titleLabel = new JBLabel("💡 使用提示 / Tips");
        titleLabel.setFont(UIUtil.getLabelFont().deriveFont(Font.BOLD, 13f));
        titleLabel.setForeground(new JBColor(new Color(230, 126, 34), new Color(241, 196, 15)));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        tipsPanel.add(titleLabel);

        tipsPanel.add(Box.createVerticalStrut(8));

        // Tips
        String[] tips = {
                "<html>1. 安装编辑器时，请勾选 <b>\"添加命令到 PATH\"</b> 选项。/ When installing, check <b>\"Add to PATH\"</b> option.</html>",
                "<html>2. 安装后需要<b>重启 IDE</b> 以获取最新的 PATH 环境变量。/ <b>Restart IDE</b> after installing editors.</html>",
                "<html>3. 所有这些编辑器都基于 VS Code，使用相同的命令行语法。/ All editors are VS Code based, sharing the same CLI syntax.</html>",
                "<html>4. 点击 <b>\"+ 添加\"</b> 按钮可以添加自定义编辑器。/ Click <b>\"+ Add\"</b> to add custom editors.</html>",
                "<html>5. 双击<b>路径</b>列可以选择可执行文件。/ Double-click <b>Path</b> column to select executable.</html>"
        };

        for (String tip : tips) {
            JBLabel tipLabel = new JBLabel(tip);
            tipLabel.setFont(UIUtil.getLabelFont().deriveFont(12f));
            tipLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            tipsPanel.add(tipLabel);
            tipsPanel.add(Box.createVerticalStrut(3));
        }

        return tipsPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(UIUtil.getPanelBackground());

        // Header
        JBLabel headerLabel = new JBLabel("编辑器配置 / Editor Configuration");
        headerLabel.setFont(UIUtil.getLabelFont().deriveFont(Font.BOLD, 14f));
        headerLabel.setForeground(new JBColor(new Color(33, 150, 243), new Color(100, 180, 255)));
        headerLabel.setBorder(JBUI.Borders.empty(5, 0, 10, 0));
        tablePanel.add(headerLabel, BorderLayout.NORTH);

        // Table with toolbar
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(editorsTable)
                .setAddAction(button -> addEditor())
                .setRemoveAction(button -> removeEditor())
                .setRemoveActionUpdater(e -> {
                    int row = editorsTable.getSelectedRow();
                    if (row >= 0 && row < editingEditors.size()) {
                        return !editingEditors.get(row).builtin;
                    }
                    return false;
                });

        JPanel decoratedTable = decorator.createPanel();
        decoratedTable.setPreferredSize(new Dimension(700, 300));
        tablePanel.add(decoratedTable, BorderLayout.CENTER);

        // Footer hint
        JBLabel footerLabel = new JBLabel(
                "<html><i>内置编辑器无法删除，只能禁用。/ Built-in editors cannot be deleted, only disabled.</i></html>");
        footerLabel.setFont(UIUtil.getLabelFont().deriveFont(11f));
        footerLabel.setForeground(JBColor.GRAY);
        footerLabel.setBorder(JBUI.Borders.emptyTop(5));
        tablePanel.add(footerLabel, BorderLayout.SOUTH);

        return tablePanel;
    }

    private void addEditor() {
        String name = JOptionPane.showInputDialog(myMainPanel,
                "请输入编辑器名称 / Enter editor name:",
                "添加编辑器 / Add Editor",
                JOptionPane.PLAIN_MESSAGE);

        if (name != null && !name.trim().isEmpty()) {
            String command = JOptionPane.showInputDialog(myMainPanel,
                    "请输入命令名称（如 code, cursor）/ Enter command name:",
                    "添加编辑器 / Add Editor",
                    JOptionPane.PLAIN_MESSAGE);

            if (command != null && !command.trim().isEmpty()) {
                EditorConfig newEditor = new EditorConfig(name.trim(), command.trim(), false);
                editingEditors.add(newEditor);
                tableModel.fireTableDataChanged();
            }
        }
    }

    private void removeEditor() {
        int row = editorsTable.getSelectedRow();
        if (row >= 0 && row < editingEditors.size()) {
            EditorConfig editor = editingEditors.get(row);
            if (!editor.builtin) {
                int result = JOptionPane.showConfirmDialog(myMainPanel,
                        "确定删除编辑器 \"" + editor.name + "\"？\nDelete editor \"" + editor.name + "\"?",
                        "确认删除 / Confirm Delete",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    editingEditors.remove(row);
                    tableModel.fireTableDataChanged();
                }
            }
        }
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return editorsTable;
    }

    /**
     * Get the current editing state of editors
     */
    public List<EditorConfig> getEditors() {
        // Stop any ongoing cell editing
        if (editorsTable.isEditing()) {
            editorsTable.getCellEditor().stopCellEditing();
        }
        return editingEditors;
    }

    /**
     * Reset to given editors list
     */
    public void setEditors(List<EditorConfig> editors) {
        this.editingEditors = new ArrayList<>();
        for (EditorConfig e : editors) {
            this.editingEditors.add(e.copy());
        }
        tableModel.setEditors(this.editingEditors);
        tableModel.fireTableDataChanged();
    }

    /**
     * Table model for editors
     */
    private static class EditorTableModel extends AbstractTableModel {
        private final String[] COLUMN_NAMES = { "启用/Enable", "名称/Name", "命令/Command", "路径/Path", "内置/Built-in" };
        private final Class<?>[] COLUMN_CLASSES = { Boolean.class, String.class, String.class, String.class,
                Boolean.class };
        private List<EditorConfig> editors;

        public EditorTableModel(List<EditorConfig> editors) {
            this.editors = editors;
        }

        public void setEditors(List<EditorConfig> editors) {
            this.editors = editors;
        }

        @Override
        public int getRowCount() {
            return editors.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMN_NAMES.length;
        }

        @Override
        public String getColumnName(int column) {
            return COLUMN_NAMES[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return COLUMN_CLASSES[columnIndex];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            // Built-in column is not editable
            if (columnIndex == 4)
                return false;
            // Name and Command for built-in editors are not editable
            if (editors.get(rowIndex).builtin && (columnIndex == 1 || columnIndex == 2)) {
                return false;
            }
            return true;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            EditorConfig editor = editors.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> editor.enabled;
                case 1 -> editor.name;
                case 2 -> editor.command;
                case 3 -> editor.path;
                case 4 -> editor.builtin;
                default -> null;
            };
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            EditorConfig editor = editors.get(rowIndex);
            switch (columnIndex) {
                case 0 -> editor.enabled = (Boolean) aValue;
                case 1 -> editor.name = (String) aValue;
                case 2 -> editor.command = (String) aValue;
                case 3 -> editor.path = (String) aValue;
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    /**
     * Custom cell renderer for path column
     */
    private static class PathCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String path = (String) value;
            if (path == null || path.isEmpty()) {
                setText("(使用 PATH / Use PATH)");
                setForeground(JBColor.GRAY);
            } else {
                setText(path);
                setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
            }
            return this;
        }
    }

    /**
     * Custom cell editor for path column with file chooser
     */
    private class PathCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel;
        private final JTextField textField;
        private final JButton browseButton;

        public PathCellEditor() {
            panel = new JPanel(new BorderLayout());
            textField = new JTextField();
            browseButton = new JButton("...");
            browseButton.setPreferredSize(new Dimension(30, 25));

            browseButton.addActionListener(e -> {
                FileChooserDescriptor descriptor = FileChooserDescriptorFactory
                        .createSingleFileOrExecutableAppDescriptor();
                descriptor.setTitle("Select Executable");
                FileChooser.chooseFile(descriptor, null, null, file -> {
                    if (file != null) {
                        textField.setText(file.getPath());
                    }
                });
            });

            panel.add(textField, BorderLayout.CENTER);
            panel.add(browseButton, BorderLayout.EAST);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            textField.setText(value != null ? (String) value : "");
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return textField.getText();
        }
    }
}
