import { UserPlus, Settings, LineChart } from "lucide-react"

const steps = [
  {
    icon: UserPlus,
    step: "01",
    title: "Create Your Account",
    description: "Sign up in seconds and set up your personal health profile with your goals, preferences, and current metrics.",
  },
  {
    icon: Settings,
    step: "02", 
    title: "Customize Your Plan",
    description: "Set your fitness goals, dietary preferences, and daily targets. BiteRight adapts to your unique needs.",
  },
  {
    icon: LineChart,
    step: "03",
    title: "Track & Transform",
    description: "Log your meals, workouts, and water intake. Watch your progress with detailed analytics and insights.",
  },
]

export function HowItWorks() {
  return (
    <section id="how-it-works" className="py-20 sm:py-28">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="mx-auto max-w-2xl text-center">
          <h2 className="text-sm font-semibold uppercase tracking-wider text-secondary">How It Works</h2>
          <p className="mt-2 text-balance text-3xl font-bold tracking-tight text-foreground sm:text-4xl">
            Start your journey in three simple steps
          </p>
        </div>

        <div className="mt-16 grid gap-8 lg:grid-cols-3">
          {steps.map((step, index) => (
            <div key={step.title} className="relative">
              {index < steps.length - 1 && (
                <div className="absolute left-1/2 top-12 hidden h-0.5 w-full -translate-x-1/2 bg-gradient-to-r from-secondary/50 to-transparent lg:block" />
              )}
              <div className="relative flex flex-col items-center text-center">
                <div className="relative mb-6">
                  <div className="flex h-24 w-24 items-center justify-center rounded-full bg-primary text-primary-foreground">
                    <step.icon className="h-10 w-10" />
                  </div>
                  <div className="absolute -bottom-2 -right-2 flex h-8 w-8 items-center justify-center rounded-full bg-accent text-sm font-bold text-accent-foreground">
                    {step.step}
                  </div>
                </div>
                <h3 className="mb-3 text-xl font-semibold text-foreground">{step.title}</h3>
                <p className="text-muted-foreground">{step.description}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}
