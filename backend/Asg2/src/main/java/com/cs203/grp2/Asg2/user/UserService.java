import java.util.List;

public interface UserService {
    
/**
 * Service interface for managing user operations.
 * 
 * This interface defines the contract for user-related business logic operations
 * including CRUD (Create, Read, Update, Delete) operations for users.
 * Implementations of this interface should handle the business logic and
 * coordinate with the data access layer.
 */
    
    /**
     * Retrieves all users from the system.
     * 
     * @return a list of all users in the system. Returns an empty list if no users exist.
     */
    List<User> listUsers();
    
    /**
     * Retrieves a specific user by its unique identifier.
     * 
     * @param id the unique identifier of the user to retrieve
     * @return the book with the specified ID
     * @throws UserNotFoundException if no user exists with the given ID
     */
    User getUser(Long id);
    
    /**
     * Creates a new user in the system.
     * 
     * @param the user to be added
     * @return the newly created user with assigned ID
     */
    User addUser(User user);
    
    /**
     * Updates an existing user with new information.
     * 
     * @param id the unique identifier of the user to update
     * @param the updated user information
     * @return the updated book with all changes applied
     * @throws BookNotFoundException if no book exists with the given ID
     */
    User updateUser(Long id, User user);

    /**
     * Removes a user from the system permanently.
     * 
     * @param id the unique identifier of the user to delete
     * @throws UserNotFoundException if no user exists with the given ID
     */
    void deleteUser(Long id);
}

