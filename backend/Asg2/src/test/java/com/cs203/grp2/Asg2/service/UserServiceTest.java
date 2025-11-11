package com.cs203.grp2.Asg2.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

import com.cs203.grp2.Asg2.models.User;
import com.google.api.core.ApiFuture;
import com.google.firebase.database.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private FirebaseDatabase firebaseDatabase;

    @Mock
    private DatabaseReference databaseReference;

    @Mock
    private DatabaseReference childReference;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Setup default mocking behavior - lenient to avoid UnnecessaryStubbingException
        lenient().when(firebaseDatabase.getReference("users")).thenReturn(databaseReference);
        lenient().when(databaseReference.child(anyString())).thenReturn(childReference);
    }

    @Test
    void testGetUserRoles_UserExists_ReturnsRole() throws ExecutionException, InterruptedException {
        // Arrange
        String userId = "testUser123";
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setRole(User.Role.ADMIN);

        // Mock the Firebase listener to return the user
        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            DataSnapshot snapshot = mock(DataSnapshot.class);
            when(snapshot.exists()).thenReturn(true);
            when(snapshot.getValue(User.class)).thenReturn(mockUser);
            listener.onDataChange(snapshot);
            return null;
        }).when(childReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        // Act
        User.Role result = userService.getUserRoles(userId);

        // Assert
        assertNotNull(result);
        assertEquals(User.Role.ADMIN, result);
        verify(databaseReference).child(userId);
    }

    @Test
    void testGetUserRoles_UserNotFound_ReturnsNull() throws ExecutionException, InterruptedException {
        // Arrange
        String userId = "nonExistentUser";

        // Mock the Firebase listener to return null (user doesn't exist)
        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            DataSnapshot snapshot = mock(DataSnapshot.class);
            lenient().when(snapshot.exists()).thenReturn(false);
            lenient().when(snapshot.getValue(User.class)).thenReturn(null);
            listener.onDataChange(snapshot);
            return null;
        }).when(childReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        // Act & Assert
        // The service now throws UserNotFoundException instead of returning null
        assertThrows(com.cs203.grp2.Asg2.exceptions.UserNotFoundException.class, () -> {
            userService.getUserRoles(userId);
        });
    }

    @Test
    void testGetUserRoles_UserExistsButNoRole_ReturnsNull() throws ExecutionException, InterruptedException {
        // Arrange
        String userId = "userWithoutRole";
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setRole(null); // No role set

        // Mock the Firebase listener
        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            DataSnapshot snapshot = mock(DataSnapshot.class);
            when(snapshot.exists()).thenReturn(true);
            when(snapshot.getValue(User.class)).thenReturn(mockUser);
            listener.onDataChange(snapshot);
            return null;
        }).when(childReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        // Act & Assert
        // The service now throws UserNotFoundException when user has no role
        assertThrows(com.cs203.grp2.Asg2.exceptions.UserNotFoundException.class, () -> {
            userService.getUserRoles(userId);
        });
    }

    @Test
    void testUpdateUserRole_UserExists_UpdatesRole() throws ExecutionException, InterruptedException {
        // Arrange
        String userId = "userToUpdate";
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setRole(User.Role.USER);

        // Mock getUserById to return existing user
        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            DataSnapshot snapshot = mock(DataSnapshot.class);
            when(snapshot.exists()).thenReturn(true);
            when(snapshot.getValue(User.class)).thenReturn(mockUser);
            listener.onDataChange(snapshot);
            return null;
        }).when(childReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        // Mock saveUser - setValueAsync doesn't need to return anything
        when(childReference.setValueAsync(any(User.class))).thenReturn(null);

        // Act
        userService.updateUserRole(userId, User.Role.ADMIN);

        // Assert
        assertEquals(User.Role.ADMIN, mockUser.getRole());
        verify(childReference).setValueAsync(mockUser);
    }

    @Test
    void testUpdateUserRole_UserNotFound_DoesNotUpdate() throws ExecutionException, InterruptedException {
        // Arrange
        String userId = "nonExistentUser";

        // Mock getUserById to return null
        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            DataSnapshot snapshot = mock(DataSnapshot.class);
            lenient().when(snapshot.exists()).thenReturn(false);
            lenient().when(snapshot.getValue(User.class)).thenReturn(null);
            listener.onDataChange(snapshot);
            return null;
        }).when(childReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        // Act & Assert
        // The service now throws UserNotFoundException when user not found for update
        assertThrows(com.cs203.grp2.Asg2.exceptions.UserNotFoundException.class, () -> {
            userService.updateUserRole(userId, User.Role.ADMIN);
        });
        verify(childReference, never()).setValueAsync(any(User.class));
    }

    @Test
    void testGetAllUsers_MultipleUsers_ReturnsAllUsers() throws ExecutionException, InterruptedException {
        // Arrange
        User user1 = new User();
        user1.setId("user1");
        user1.setEmail("user1@example.com");
        user1.setRole(User.Role.USER);

        User user2 = new User();
        user2.setId("user2");
        user2.setEmail("user2@example.com");
        user2.setRole(User.Role.ADMIN);

        User user3 = new User();
        user3.setId("user3");
        user3.setEmail("user3@example.com");
        user3.setRole(User.Role.USER);

        // Mock DataSnapshot with multiple children
        DataSnapshot childSnapshot1 = mock(DataSnapshot.class);
        when(childSnapshot1.getValue(User.class)).thenReturn(user1);

        DataSnapshot childSnapshot2 = mock(DataSnapshot.class);
        when(childSnapshot2.getValue(User.class)).thenReturn(user2);

        DataSnapshot childSnapshot3 = mock(DataSnapshot.class);
        when(childSnapshot3.getValue(User.class)).thenReturn(user3);

        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            DataSnapshot parentSnapshot = mock(DataSnapshot.class);
            when(parentSnapshot.getChildren()).thenReturn(List.of(childSnapshot1, childSnapshot2, childSnapshot3));
            listener.onDataChange(parentSnapshot);
            return null;
        }).when(databaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));
        assertTrue(result.contains(user3));
    }

    @Test
    void testGetAllUsers_NoUsers_ReturnsEmptyList() throws ExecutionException, InterruptedException {
        // Arrange - Mock empty snapshot
        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            DataSnapshot parentSnapshot = mock(DataSnapshot.class);
            when(parentSnapshot.getChildren()).thenReturn(List.of()); // No children
            listener.onDataChange(parentSnapshot);
            return null;
        }).when(databaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        // Act & Assert
        // The service now throws UserNotFoundException when no users found
        assertThrows(com.cs203.grp2.Asg2.exceptions.UserNotFoundException.class, () -> {
            userService.getAllUsers();
        });
    }

    @Test
    void testGetAllUsers_WithNullUsers_FiltersOutNulls() throws ExecutionException, InterruptedException {
        // Arrange
        User user1 = new User();
        user1.setId("user1");
        user1.setRole(User.Role.USER);

        DataSnapshot childSnapshot1 = mock(DataSnapshot.class);
        when(childSnapshot1.getValue(User.class)).thenReturn(user1);

        DataSnapshot childSnapshot2 = mock(DataSnapshot.class);
        when(childSnapshot2.getValue(User.class)).thenReturn(null); // Null user

        DataSnapshot childSnapshot3 = mock(DataSnapshot.class);
        when(childSnapshot3.getValue(User.class)).thenReturn(null); // Null user

        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            DataSnapshot parentSnapshot = mock(DataSnapshot.class);
            when(parentSnapshot.getChildren()).thenReturn(List.of(childSnapshot1, childSnapshot2, childSnapshot3));
            listener.onDataChange(parentSnapshot);
            return null;
        }).when(databaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(user1, result.get(0));
    }

    @Test
    void testSaveUser_CallsFirebaseSetValue() {
        // Arrange
        String userId = "userToSave";
        User userToSave = new User();
        userToSave.setId(userId);
        userToSave.setEmail("test@example.com");
        userToSave.setRole(User.Role.USER);

        when(childReference.setValueAsync(any(User.class))).thenReturn(null);

        // Act
        userService.saveUser(userId, userToSave);

        // Assert
        verify(databaseReference).child(userId);
        verify(childReference).setValueAsync(userToSave);
    }

    @Test
    void testGetOrCreateUser_NewUser_CreatesUser() throws ExecutionException, InterruptedException {
        // Arrange
        String userId = "newUser123";
        String email = "new@example.com";
        String username = "NewUser";

        // Mock getUserById to return null (user doesn't exist)
        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            DataSnapshot snapshot = mock(DataSnapshot.class);
            lenient().when(snapshot.exists()).thenReturn(false);
            lenient().when(snapshot.getValue(User.class)).thenReturn(null);
            listener.onDataChange(snapshot);
            return null;
        }).when(childReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        // Mock setValueAsync to return a completed future
        @SuppressWarnings("unchecked")
        ApiFuture<Void> mockFuture = mock(ApiFuture.class);
        lenient().when(mockFuture.get()).thenReturn(null);
        lenient().when(childReference.setValueAsync(any(User.class))).thenReturn(mockFuture);

        // Act
        User result = userService.getOrCreateUser(userId, email, username);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(email, result.getEmail());
        assertEquals(username, result.getUsername());
        assertEquals(User.Role.USER, result.getRole());
        verify(childReference).setValueAsync(any(User.class));
    }

    @Test
    void testGetOrCreateUser_ExistingUser_ReturnsExistingUser() throws ExecutionException, InterruptedException {
        // Arrange
        String userId = "existingUser";
        String email = "existing@example.com";
        String username = "ExistingUser";

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail(email);
        existingUser.setUsername(username);
        existingUser.setRole(User.Role.ADMIN);

        // Mock getUserById to return existing user
        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            DataSnapshot snapshot = mock(DataSnapshot.class);
            when(snapshot.exists()).thenReturn(true);
            when(snapshot.getValue(User.class)).thenReturn(existingUser);
            listener.onDataChange(snapshot);
            return null;
        }).when(childReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        // Act
        User result = userService.getOrCreateUser(userId, email, username);

        // Assert
        assertNotNull(result);
        assertEquals(existingUser, result);
        assertEquals(User.Role.ADMIN, result.getRole()); // Should retain existing role
        verify(childReference, never()).setValueAsync(any(User.class)); // Should not create new user
    }
}
