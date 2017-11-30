package com.fqxyi.action;

import com.fqxyi.model.PropertiesKey;
import com.intellij.ide.util.PropertiesComponent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;

/**
 * 配置弹窗
 */
public class ButterKnifeCodeDialog extends JDialog {

    private JPanel contentPane;         // layout
    private JCheckBox chbIsR2;          // selected R2.id.xxx ; unselected R.id.xxx
    private JTable tableViews;          // id list
    private JButton btnSelectNone;      // 全不选
    private JButton btnNegativeSelect;  // 反选
    private JButton btnSelectAll;       // 全选
    private JTextArea textCode;         // 最终生成的代码
    private JButton btnCopyCode;        // copy code
    private JButton btnClose;           // close dialog

    private onClickListener onClickListener;

    public ButterKnifeCodeDialog() {
        setContentPane(contentPane);
        setModal(true);

        // 默认是 R.id.xxx
        chbIsR2.setSelected(PropertiesComponent.getInstance().getBoolean(PropertiesKey.IS_R_2, false));

        initListener();

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // call onOK() on ENTER
        contentPane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onClickListener != null) {
                    onClickListener.onOK();
                }
                ButterKnifeCodeDialog.this.onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void initListener() {
        chbIsR2.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (onClickListener != null) {
                    onClickListener.onSwitchIsR2(chbIsR2.isSelected());
                    PropertiesComponent.getInstance().setValue(PropertiesKey.IS_R_2, chbIsR2.isSelected());
                }
            }
        });

        btnSelectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onClickListener != null) {
                    onClickListener.onSelectAll();
                }
            }
        });
        btnSelectNone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onClickListener != null) {
                    onClickListener.onSelectNone();
                }
            }
        });
        btnNegativeSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onClickListener != null) {
                    onClickListener.onNegativeSelect();
                }
            }
        });

        btnCopyCode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (onClickListener != null) {
                    onClickListener.onOK();
                }
                onCancel();
            }
        });
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
    }

    public void setTextCode(String codeStr) {
        textCode.setText(codeStr);
    }

    public String getTextCode() {
        if (textCode == null) {
            return "";
        }
        return textCode.getText();
    }

    public void setModel(DefaultTableModel model) {
        tableViews.setModel(model);
        tableViews.getColumnModel().getColumn(0).setPreferredWidth(20);
    }

    private void onCancel() {
        dispose();
        if (onClickListener != null) {
            onClickListener.onFinish();
        }
    }

    public void setOnClickListener(onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface onClickListener {
        void onSwitchIsR2(boolean r2);

        void onSelectAll();
        void onSelectNone();
        void onNegativeSelect();

        void onOK();
        void onFinish();
    }

    public static void main(String[] args) {
        ButterKnifeCodeDialog dialog = new ButterKnifeCodeDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
