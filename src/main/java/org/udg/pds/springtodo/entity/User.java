package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity(name = "users")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = User.class)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email", "username"}))
public class User extends BaseEntity implements Serializable {
  /**
   * Default value included to remove warning. Remove or modify at will. *
   */
  private static final long serialVersionUID = 1L;

  public User() {
  }

  public User(String username, String email, String password, String descripcio) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.descripcio = descripcio;
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
  private String descripcio;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
  private Collection<Task> tasks;

  @OneToMany(mappedBy = "usuari")
  private Collection<Recepta> receptes;

  @ManyToMany
  private Collection<Recepta> receptesPreferides;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }

  @JsonIgnore
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

  @JsonView(Views.Public.class)
  public String getDescripcio() {
        return descripcio;
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

  @JsonIgnore
  public Collection<Recepta> getReceptesPreferides(){
        return receptesPreferides;
  }

  public void addReceptaPreferida(Recepta recepta){
        receptesPreferides.add(recepta);
  }

  public void removeReceptaPreferida(Recepta recepta){
        receptesPreferides.remove(recepta);
  }
}
