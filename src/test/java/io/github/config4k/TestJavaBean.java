package io.github.config4k;

import java.util.Objects;

public class TestJavaBean {

  private String name;

  private int age;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof TestJavaBean)) {
      return false;
    }
    TestJavaBean that = (TestJavaBean) o;
    return age == that.age && Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, age);
  }
}
