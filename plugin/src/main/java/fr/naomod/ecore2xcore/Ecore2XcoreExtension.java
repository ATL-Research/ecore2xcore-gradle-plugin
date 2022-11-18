package fr.naomod.ecore2xcore;

public class Ecore2XcoreExtension {
    private String sourceDir;
    private String targetDir;
    private String basePackage;
    
    public String getSourceDir() {
        return sourceDir;
    }
    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }
    public String getTargetDir() {
        return targetDir;
    }
    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }
    public String getBasePackage() {
        return basePackage;
    }
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
}
