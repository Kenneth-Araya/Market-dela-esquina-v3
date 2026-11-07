package cl.duoc.msVenta.exeptions;

public class RecursoNoEncontradoException extends RuntimeException{
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}

