const stats = [
  { value: "50K+", label: "Active Users" },
  { value: "2M+", label: "Meals Logged" },
  { value: "500K+", label: "Workouts Tracked" },
  { value: "98%", label: "User Satisfaction" },
]

export function Stats() {
  return (
    <section className="bg-primary py-16">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-4">
          {stats.map((stat) => (
            <div key={stat.label} className="text-center">
              <div className="text-4xl font-bold text-primary-foreground sm:text-5xl">{stat.value}</div>
              <div className="mt-2 text-sm text-primary-foreground/80">{stat.label}</div>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}
