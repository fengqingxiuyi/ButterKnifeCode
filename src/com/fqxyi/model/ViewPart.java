package com.fqxyi.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ButterKnife Code 结构
 */
public class ViewPart {

    private static final String OUTPUT_DECLARE_STRING = "@BindView(%s.id.%s) %s %s;\n";

    private boolean selected;
    private String id;
    private String type;
    private String name;

    /**
     * 默认全选
     */
    public ViewPart() {
        selected = true;
    }

    public String getOutputDeclareString(boolean isR2) {
        if (isR2) {
            return String.format(OUTPUT_DECLARE_STRING, "R2", id, type, name);
        } else {
            return String.format(OUTPUT_DECLARE_STRING, "R", id, type, name);
        }
    }

    private void generateName(String id) {
        Pattern pattern = Pattern.compile("_([a-zA-Z])");
        Matcher matcher = pattern.matcher(id);

        char[] chars = id.toCharArray();
        while (matcher.find()) {
            int index = matcher.start(1);
            chars[index] = Character.toUpperCase(chars[index]);
        }
        String name = String.copyValueOf(chars);
        name = name.replaceAll("_", "");
        setName(name);
    }

    public void resetName() {
        generateName(id);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        generateName(id);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        String[] packages = type.split("\\.");
        if (packages.length > 1) {
            this.type = packages[packages.length - 1];
        } else {
            this.type = type;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ViewPart{" +
                "selected=" + selected +
                ", id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
