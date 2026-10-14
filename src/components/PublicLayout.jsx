import { Outlet } from 'react-router-dom'
import Navigation from './Navigation'

function PublicLayout() {
  return (
    <>
      <Navigation />
      <Outlet />
    </>
  )
}

export default PublicLayout
