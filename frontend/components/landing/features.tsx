import { 
  Target, 
  Utensils, 
  Activity, 
  Droplets, 
  Calculator,
  BarChart3 
} from "lucide-react"

const features = [
  {
    icon: Target,
    title: "Set Fitness Goals",
    description: "Define personalized fitness objectives and track your progress towards achieving them with smart milestones.",
  },
  {
    icon: Utensils,
    title: "Meal & Recipe Tracking",
    description: "Log your daily meals, discover new recipes, and manage your dietary intake with our comprehensive food database.",
  },
  {
    icon: Activity,
    title: "Workout Logging",
    description: "Record your physical activities, from cardio to strength training, with detailed exercise tracking and history.",
  },
  {
    icon: Droplets,
    title: "Hydration Monitoring",
    description: "Stay on top of your water intake with smart reminders and daily hydration goals tailored to your needs.",
  },
  {
    icon: Calculator,
    title: "Calorie Calculation",
    description: "Automatically calculate caloric expenditure based on your exercises, weight history, and metabolic rate.",
  },
  {
    icon: BarChart3,
    title: "Daily Summaries",
    description: "Get comprehensive daily reports integrating nutrition and workout data for a complete health overview.",
  },
]

export function Features() {
  return (
    <section id="features" className="bg-card py-20 sm:py-28">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="mx-auto max-w-2xl text-center">
          <h2 className="text-sm font-semibold uppercase tracking-wider text-secondary">Features</h2>
          <p className="mt-2 text-balance text-3xl font-bold tracking-tight text-foreground sm:text-4xl">
            Everything you need for a healthier you
          </p>
          <p className="mt-4 text-pretty text-lg text-muted-foreground">
            Comprehensive tools designed to help you reach your health and fitness goals.
          </p>
        </div>

        <div className="mt-16 grid gap-8 sm:grid-cols-2 lg:grid-cols-3">
          {features.map((feature, index) => (
            <div 
              key={feature.title}
              className="group relative rounded-2xl border border-border bg-background p-8 transition-all hover:border-secondary/50 hover:shadow-lg"
            >
              <div className="mb-4 inline-flex h-12 w-12 items-center justify-center rounded-xl bg-secondary/10 text-secondary transition-colors group-hover:bg-secondary group-hover:text-secondary-foreground">
                <feature.icon className="h-6 w-6" />
              </div>
              <h3 className="mb-2 text-xl font-semibold text-foreground">{feature.title}</h3>
              <p className="text-muted-foreground">{feature.description}</p>
              {index === 0 && (
                <div className="absolute -right-2 -top-2 rounded-full bg-accent px-3 py-1 text-xs font-medium text-accent-foreground">
                  Popular
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}
