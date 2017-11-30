package com.fqxyi.action;

import com.fqxyi.model.PropertiesKey;
import com.fqxyi.model.ViewPart;
import com.fqxyi.util.ActionUtil;
import com.fqxyi.util.Utils;
import com.fqxyi.util.ViewSaxHandler;
import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiFile;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;

/**
 * Created by fqxyi
 * 在 Code 页面 右击生成 ButterKnife Code
 */
public class ButterKnifeCodeAction extends BaseGenerateAction {

    private boolean isR2 = false;

    private ViewSaxHandler viewSaxHandler;
    private List<ViewPart> viewParts;
    private DefaultTableModel tableModel;
    private ButterKnifeCodeDialog codeDialog;

    public ButterKnifeCodeAction() {
        super(null);
    }

    public ButterKnifeCodeAction(CodeInsightActionHandler handler) {
        super(handler);
    }

    /**
     * 启动时触发
     */
    @Override
    public void actionPerformed(AnActionEvent event) {
        isR2 = PropertiesComponent.getInstance().getBoolean(PropertiesKey.IS_R_2, false);

        viewSaxHandler = new ViewSaxHandler();
        if (codeDialog == null) {
            codeDialog = new ButterKnifeCodeDialog();
        }

        getViewList(event);

        updateTable();

        codeDialog.setTitle("ButterKnifeCode in XML");
        codeDialog.setOnClickListener(onClickListener);
        codeDialog.pack();
        codeDialog.setLocationRelativeTo(WindowManager.getInstance().getFrame(event.getProject()));
        codeDialog.setVisible(true);
    }

    /**
     * get views list
     *
     * @param event 触发事件
     */
    private void getViewList(AnActionEvent event) {
        PsiFile psiFile = event.getData(LangDataKeys.PSI_FILE);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        PsiFile layout = Utils.getLayoutFileFromCaret(editor, psiFile);
        if (psiFile == null || editor == null) {
            return;
        }
        String contentStr = psiFile.getText();
        if (layout != null) {
            contentStr = layout.getText();
        }
        if (psiFile.getParent() != null) {
            String javaPath = psiFile.getContainingDirectory().toString().replace("PsiDirectory:", "");
            String javaPathKey = "src" + File.separator + "main" + File.separator + "java";
            int indexOf = javaPath.indexOf(javaPathKey);
            String layoutPath = "";
            if (indexOf != -1) {
                layoutPath = javaPath.substring(0, indexOf) + "src" + File.separator + "main" + File.separator + "res" + File.separator + "layout";
            }
            viewSaxHandler.setLayoutPath(layoutPath);
            viewSaxHandler.setProject(event.getProject());
        }
        viewParts = ActionUtil.getViewPartList(viewSaxHandler, contentStr);
    }

    /**
     * ButterKnifeCodeDialog 回调监听器
     */
    private ButterKnifeCodeDialog.onClickListener onClickListener = new ButterKnifeCodeDialog.onClickListener() {

        @Override
        public void onSwitchIsR2(boolean r2) {
            isR2 = r2;
            generateCode();
        }

        @Override
        public void onSelectAll() {
            for (ViewPart viewPart : viewParts) {
                viewPart.setSelected(true);
            }
            updateTable();
        }

        @Override
        public void onSelectNone() {
            for (ViewPart viewPart : viewParts) {
                viewPart.setSelected(false);
            }
            updateTable();
        }

        @Override
        public void onNegativeSelect() {
            for (ViewPart viewPart : viewParts) {
                viewPart.setSelected(!viewPart.isSelected());
            }
            updateTable();
        }

        @Override
        public void onOK() {
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable tText = new StringSelection(codeDialog.getTextCode());
            clip.setContents(tText, null);
        }

        @Override
        public void onFinish() {
            viewParts = null;
            viewSaxHandler = null;
            codeDialog = null;
        }
    };


    /**
     * 生成 ButterKnife Code 代码
     */
    private void generateCode() {
        codeDialog.setTextCode(ActionUtil.generateCode(viewParts, isR2));
    }

    /**
     * 更新 View 表格
     */
    public void updateTable() {
        if (viewParts == null || viewParts.size() == 0) {
            return;
        }
        tableModel = ActionUtil.getTableModel(viewParts, tableModelListener);
        codeDialog.setModel(tableModel);
        generateCode();
    }

    TableModelListener tableModelListener = new TableModelListener() {
        @Override
        public void tableChanged(TableModelEvent event) {
            if (tableModel == null) {
                return;
            }
            int row = event.getFirstRow();
            int column = event.getColumn();
            if (column == 0) {
                Boolean isSelected = (Boolean) tableModel.getValueAt(row, column);
                viewSaxHandler.getViewPartList().get(row).setSelected(isSelected);
                ButterKnifeCodeAction.this.generateCode();
            }
        }
    };

}
