package com.proyecto.ecommerce.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.proyecto.ecommerce.validation.Create;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "Usuario")
@Data //Genera mediante lombok getters, setters, toString, equals y hasCode
@NoArgsConstructor //Genera un constructor sin argumentos mediante lombok
@AllArgsConstructor //Genera un constructor con todos los argumentos
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @NotBlank(message = "El usuario no puede estar vacío")
    @Column(nullable = false, unique = true)
    private String username;

    // Contraseña encriptada (bcrypt) => se guarda cifrada en la BD
    // En la creación, la contraseña es obligatoria, pero en la actualización puede omitirse
    @NotBlank(message = "La contraseña es obligatoria", groups = Create.class)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Email(message = "Debe ser un correo valido")
    @NotBlank(message = "El correo no puede estar vacio")
    @Column(nullable = false, unique = true)
    private String correo;


    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Column(nullable = false)
    private String apellido;

    @Column
    private String direccion;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "usuarios_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    // Se indica que ignore la propiedad 'usuarios' en Role para evitar recursividad
    @JsonIgnoreProperties({"usuarios"})
    private List<Role> roles;

    // Indica si el usuario está habilitado
    private boolean enabled = true;

    // Campo transitorio para indicar si se crea como admin
    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean admin;


    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("usuario")
    @ToString.Exclude
    private List<Pedido> pedidos;

    //  Método para limpiar las relaciones ANTES de eliminar el usuario
    @PreRemove
    private void removeRoles() {
        this.roles.clear();
    }

}
