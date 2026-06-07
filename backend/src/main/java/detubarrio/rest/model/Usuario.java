package detubarrio.rest.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
// ✅ NUEVOS IMPORTS PARA EL BORRADO EN CASCADA
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 20)
    private RolUsuario rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comercio")
    @OnDelete(action = OnDeleteAction.CASCADE) // ✅ ESTO ES LO QUE FALTA: Configura el ON DELETE CASCADE real en la BD
    private Comercio comercio;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "cliente_favoritos_comercio",
        joinColumns = @JoinColumn(name = "id_cliente", referencedColumnName = "id_usuario"),
        inverseJoinColumns = @JoinColumn(name = "id_comercio"),
        foreignKey = @ForeignKey(name = "none"),
        inverseForeignKey = @ForeignKey(name = "none")
    )
    @Builder.Default 
    private List<Comercio> favoritos = new ArrayList<>();
}