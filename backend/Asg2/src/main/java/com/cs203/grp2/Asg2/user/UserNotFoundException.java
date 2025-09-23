public class UserNotFoundException {
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404 Error
    public class ReviewNotFoundException extends RuntimeException{
        private static final long serialVersionUID = 1L;

        public ReviewNotFoundException(Long id) {
            super("Could not find user " + id);
        }
    }
}
