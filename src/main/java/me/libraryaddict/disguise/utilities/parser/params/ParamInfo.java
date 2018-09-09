package me.libraryaddict.disguise.utilities.parser.params;

import me.libraryaddict.disguise.utilities.TranslateType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by libraryaddict on 7/09/2018.
 */
public abstract class ParamInfo {
    private Class paramClass;
    private String descriptiveName;
    private String name;
    private Map<String, Object> possibleValues;
    /**
     * Used for translations, namely ItemStack and it's 'Glowing' and 'null' counterparts
     */
    private String[] otherValues;
    private String description;

    public ParamInfo(Class paramClass, String name, String description) {
        this(paramClass, name, name, description);
    }

    public ParamInfo(Class paramClass, String name, String descriptiveName, String description) {
        this.name = name;
        this.paramClass = paramClass;
        this.descriptiveName = descriptiveName;
        this.description = description;
    }

    public ParamInfo(Class paramClass, String name, String description, Enum[] possibleValues) {
        this(paramClass, name, name, description);
    }

    public ParamInfo(Class paramClass, String name, String descriptiveName, String description, Enum[] possibleValues) {
        this(paramClass, name, descriptiveName, description);

        this.possibleValues = new HashMap<>();

        for (Enum anEnum : possibleValues) {
            this.getValues().put(anEnum.name(), anEnum);
        }
    }

    public ParamInfo(Class paramClass, String name, String description, String[] possibleValues) {
        this(paramClass, name, name, description);
    }

    public ParamInfo(Class paramClass, String name, String descriptiveName, String description,
            String[] possibleValues) {
        this(paramClass, name, descriptiveName, description);

        this.possibleValues = new HashMap<>();

        for (String value : possibleValues) {
            getValues().put(value, value);
        }
    }

    public boolean canTranslateValues() {
        return getValues() != null;
    }

    public String[] getOtherValues() {
        return this.otherValues;
    }

    public void setOtherValues(String... otherValues) {
        this.otherValues = otherValues;
    }

    public boolean canReturnNull() {
        return false;
    }

    protected abstract Object fromString(String string);

    public Object fromString(List<String> arguments) {
        // Don't consume a string immediately, if it errors we need to check other param types
        String string = arguments.get(0);

        Object value = fromString(string);

        arguments.remove(0);

        return value;
    }

    public int getMinArguments() {
        return 1;
    }

    public boolean hasValues() {
        return getValues() != null;
    }

    private Class getParamClass() {
        return paramClass;
    }

    public boolean isParam(Class paramClass) {
        return getParamClass() == paramClass;
    }

    public String getName() {
        return TranslateType.DISGUISE_OPTIONS_PARAMETERS.get(getRawName());
    }

    public String getDescriptiveName() {
        return TranslateType.DISGUISE_OPTIONS_PARAMETERS.get(getRawDescriptiveName());
    }

    public String getRawName() {
        return this.name;
    }

    public String getRawDescriptiveName() {
        return descriptiveName;
    }

    public String getDescription() {
        return TranslateType.DISGUISE_OPTIONS_PARAMETERS.get(getRawDescription());
    }

    public String getRawDescription() {
        return description;
    }

    public Map<String, Object> getValues() {
        return this.possibleValues;
    }

    public Set<String> getEnums(String tabComplete) {
        return getValues().keySet();
    }
}
