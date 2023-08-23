package com.example.proyectosemestral;

public class Traduccion {
  private String source;
  private String target;
  private String text;
  private String translation;

  public Traduccion() {
  }

  public Traduccion(String source, String target, String text, String translation) {
    this.source = source;
    this.target = target;
    this.text = text;
    this.translation = translation;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getTranslation() {
    return translation;
  }

  public void setTranslation(String translation) {
    this.translation = translation;
  }
}
