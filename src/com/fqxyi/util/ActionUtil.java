package com.fqxyi.util;

import com.fqxyi.model.ViewPart;
import org.xml.sax.SAXException;

import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 封装 ViewSaxHandler 工具类
 */
public class ActionUtil {

    private final static String[] HEADERS = {"selected", "id", "type", "name"};

    /**
     * 生成结构为 @BindView(%s.id.%s) %s %s;\n 的代码集合
     *
     * @param viewSaxHandler ViewSaxHandler
     * @param oriContact     Layout文件内容
     * @return 结构为 ViewPart 的数据集合
     */
    public static List<ViewPart> getViewPartList(ViewSaxHandler viewSaxHandler, String oriContact) {
        try {
            viewSaxHandler.createViewList(oriContact);
            return viewSaxHandler.getViewPartList();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<ViewPart>();
    }

    /**
     * 生成最终需要的代码，显示在textCode中
     * @param viewParts 结构为 ViewPart 的数据集合
     * @param isR2 true R2 ; false R
     * @return 最终需要的代码
     */
    public static String generateCode(List<ViewPart> viewParts, boolean isR2) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ViewPart viewPart : viewParts) {
            if (viewPart.isSelected()) {
                stringBuilder.append(viewPart.getOutputDeclareString(isR2));
            }
        }
        return stringBuilder.toString();
    }

    /**
     * TableView 展示数据
     *
     * @param viewParts          结构为 ViewPart 的数据集合
     * @param tableModelListener TableView 监听器
     * @return DefaultTableModel
     */
    public static DefaultTableModel getTableModel(List<ViewPart> viewParts, TableModelListener tableModelListener) {
        DefaultTableModel tableModel;
        int size = viewParts.size();
        Object[][] cellData = new Object[size][4];
        for (int i = 0; i < size; i++) {
            ViewPart viewPart = viewParts.get(i);
            for (int j = 0; j < 4; j++) {
                switch (j) {
                    case 0:
                        cellData[i][j] = viewPart.isSelected();
                        break;
                    case 1:
                        cellData[i][j] = viewPart.getId();
                        break;
                    case 2:
                        cellData[i][j] = viewPart.getType();
                        break;
                    case 3:
                        cellData[i][j] = viewPart.getName();
                        break;
                }
            }
        }

        tableModel = new DefaultTableModel(cellData, HEADERS) {
            final Class[] typeArray = {Boolean.class, Object.class, Object.class, Object.class};

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }

            @SuppressWarnings("rawtypes")
            public Class getColumnClass(int column) {
                return typeArray[column];
            }
        };
        tableModel.addTableModelListener(tableModelListener);
        return tableModel;
    }

}
