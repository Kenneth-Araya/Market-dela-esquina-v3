package cl.duoc.ms_inventario.exception;

public class RecursoNoEncontradoException extends RuntimeException{
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}