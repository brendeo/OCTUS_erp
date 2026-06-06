import { NavLink } from 'react-router-dom'

const tabs = [
  { to: '/dashboard', label: 'DashBoard' },
  { to: '/produtos', label: 'Estoque de Produtos' },
  { to: '/contabilidade', label: 'Finanças' },
]

export function NavTabs() {
  return (
    <nav className="border-b border-gray-200 bg-white shadow-sm">
      <div className="mx-auto flex max-w-7xl gap-1 px-4 sm:px-6 lg:px-8">
        {tabs.map((tab) => (
          <NavLink
            key={tab.to}
            to={tab.to}
            className={({ isActive }) =>
              `my-2 rounded-lg px-4 py-2 text-sm font-medium transition ${
                isActive
                  ? 'bg-action text-white shadow-sm'
                  : 'text-gray-500 hover:bg-gray-100 hover:text-gray-800'
              }`
            }
          >
            {tab.label}
          </NavLink>
        ))}
      </div>
    </nav>
  )
}
