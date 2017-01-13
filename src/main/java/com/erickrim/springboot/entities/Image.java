package com.erickrim.springboot.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * Created by krime on 1/9/17.
 */
@Entity
public class Image {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @OneToOne
    private User owner;

    private Image() {} // required by jpa

    public Image(String name, User owner) {

        this.name = name;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
