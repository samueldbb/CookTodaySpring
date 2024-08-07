package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity(name = "users")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email", "username"}))
public class User extends BaseEntity implements Serializable {
  /**
   * Default value included to remove warning. Remove or modify at will. *
   */
  private static final long serialVersionUID = 1L;

  public User() {
  }

  public User(String username, String email, String password, String ubicacio) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.ubicacio = ubicacio;
    this.tasks = new ArrayList<>();
    this.receptes = new ArrayList<>();
  }


  @NotNull
  private String username;

  @NotNull
  private String email;

  @NotNull
  private String password;

  @NotNull
  private String ubicacio;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
  private Collection<Task> tasks;

  @OneToMany
  private Collection<Recepta> receptes;

  @JsonView(Views.Complete.class)
  public Collection<Recepta> getReceptes() {
      return receptes;
  }

  @JsonView(Views.Private.class)
  public Long getId() {
    return id;
  }

  @JsonView(Views.Private.class)
  public String getEmail() {
    return email;
  }

  @JsonView(Views.Public.class)
  public String getUsername() {
    return username;
  }

  @JsonIgnore
  public String getPassword() {
    return password;
  }

  @JsonView(Views.Complete.class)
  public Collection<Task> getTasks() {
    // Since tasks is collection controlled by JPA, it has LAZY loading by default. That means
    // that you have to query the object (calling size(), for example) to get the list initialized
    // More: http://www.javabeat.net/jpa-lazy-eager-loading/
    tasks.size();
    return tasks;
  }

  public void addTask(Task task) {
    tasks.add(task);
  }

  public void addRecepta(Recepta recepta) {
      receptes.add(recepta);
  }
}
