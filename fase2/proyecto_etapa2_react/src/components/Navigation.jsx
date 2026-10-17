import { Link, NavLink } from 'react-router-dom'
import { Navbar, Nav, Container } from 'react-bootstrap'

// TODO: agregar estilos, ícono del carrito y botones de Login/Registro
function Navigation() {
  return (
    <Navbar expand="lg" bg="light">
      <Container>
        <Navbar.Brand as={Link} to="/home">Market De La Esquina</Navbar.Brand>
        <Navbar.Toggle aria-controls="navMenu" />
        <Navbar.Collapse id="navMenu">
          <Nav className="me-auto">
            <Nav.Link as={NavLink} to="/home">Inicio</Nav.Link>
            <Nav.Link as={NavLink} to="/productos">Productos</Nav.Link>
            <Nav.Link as={NavLink} to="/nosotros">Nosotros</Nav.Link>
            <Nav.Link as={NavLink} to="/blogs">Blogs</Nav.Link>
            <Nav.Link as={NavLink} to="/contacto">Contacto</Nav.Link>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  )
}

export default Navigation
