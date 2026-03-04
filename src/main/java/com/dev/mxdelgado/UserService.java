package com.dev.mxdelgado;

import java.util.*;

/**
 *Servicio encargado de gestionar los perfiles de usuario del sistema.
 *Responsabilidades principales
 *Crear perfiles de usuario
 *Buscar usuarios existentes
 *Listar todos los perfiles
 *Los usuarios se almacenan en memoria utilizando un {@link Map},
 *donde la clave es el nombre de usuario normalizado.
 */
public class UserService {

    /**
     *Mapa que almacena los usuarios registrados.
     *Clave: nombre de usuario normalizado (sin mayúsculas ni espacios).
     *Valor: objeto {@link User}.
     *Se usa {@link LinkedHashMap} para mantener el orden de inserción
     *cuando se listan los perfiles.
     */
    private final Map<String, User> users = new LinkedHashMap<>();

    /**
     *Busca un usuario por nombre o lo crea si no existe.
     *Este método utiliza {@code computeIfAbsent} para:
     *Retornar el usuario existente si ya está registrado
     *Crear uno nuevo si no existe
     * @param username nombre del usuario
     * @return usuario existente o recién creado
     */
    public User getOrCreate(String username) {
        String key = norm(username);
        return users.computeIfAbsent(key, k -> new User(username));
    }

    /**
     *Busca un usuario existente.
     *@param username nombre del usuario
     *@return el usuario encontrado o {@code null} si no existe
     */
    public User find(String username) {
        return users.get(norm(username));
    }

     /**
     *Devuelve la lista de todos los perfiles registrados.
     *Se devuelve una copia de la lista para evitar modificar
     *directamente la colección interna.
     *@return lista de usuarios registrados
     */
    public List<User> listUsers() {
        return new ArrayList<>(users.values());
    }

     /**
     *Indica si no hay perfiles registrados en el sistema.
     *@return true si no existen usuarios
     */
    public boolean isEmpty() {
        return users.isEmpty();
    }

     /**
     *Normaliza un nombre de usuario para usarlo como clave en el mapa.
     *Convierte el texto a minúsculas y elimina espacios al inicio y final.
     *que en realidad deberían representar al mismo usuario.
     *@param s texto original
     *@return texto normalizado
     */
    private static String norm(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
    }
}