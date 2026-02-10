"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import { 
  ArrowRight, 
  ArrowLeft, 
  Loader2, 
  Target, 
  User, 
  Scale,
  Activity,
  Sparkles,
  Check,
  CalendarIcon
} from "lucide-react"

import { format, differenceInCalendarDays, parseISO, startOfToday } from "date-fns"
import { Calendar } from "@/components/ui/calendar"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { cn } from "@/lib/utils"

const CALORIES_PER_KG_FAT = 7700
const MIN_CALORIES_SAFE_FLOOR = 1200 

const STEPS = [
  { id: 1, title: "Personal Info", icon: User },
  { id: 2, title: "Body Stats", icon: Scale },
  { id: 3, title: "Lifestyle", icon: Activity },
  { id: 4, title: "Your Goal", icon: Target },
]

const LIFESTYLE_OPTIONS = [
  { value: "sedentary", label: "Sedentary", description: "Little to no exercise, desk job" },
  { value: "light", label: "Lightly Active", description: "Light exercise 1-3 days/week" },
  { value: "moderate", label: "Moderately Active", description: "Moderate exercise 3-5 days/week" },
  { value: "active", label: "Very Active", description: "Hard exercise 6-7 days/week" },
  { value: "athlete", label: "Athlete", description: "Very hard exercise, physical job" },
]

const GOAL_OPTIONS = [
  { value: "lose", label: "Lose Weight", description: "Reduce body fat while maintaining muscle" },
  { value: "maintain", label: "Maintain Weight", description: "Keep current weight, improve fitness" },
  { value: "gain", label: "Gain Weight", description: "Build muscle and increase mass" },
]

export default function OnboardingPage() {
  const router = useRouter()
  const [currentStep, setCurrentStep] = useState(1)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState("")

  const [name, setName] = useState("")
  const [surname, setSurname] = useState("")
  const [age, setAge] = useState("")

  const [weight, setWeight] = useState("")
  const [height, setHeight] = useState("")

  const [lifestyle, setLifestyle] = useState("moderate")

  const [goalType, setGoalType] = useState("maintain")
  const [goalWeight, setGoalWeight] = useState("")
  const [goalDate, setGoalDate] = useState<Date | undefined>(undefined)

  const calculateBMI = () => {
    if (weight && height) {
      const heightM = Number(height) / 100
      return Number((Number(weight) / (heightM * heightM)).toFixed(1))
    }
    return 0
  }

  const calculateRecommendedCalories = () => {
    if (!weight || !height || !age) return 2000

    const bmr = 10 * Number(weight) + 6.25 * Number(height) - 5 * Number(age) + 5

    const activityMultipliers: Record<string, number> = {
      sedentary: 1.2,
      light: 1.375,
      moderate: 1.55,
      active: 1.725,
      athlete: 1.9,
    }
    const tdee = bmr * (activityMultipliers[lifestyle] || 1.55)

    let dailyAdjustment = 0

    if (goalType === "maintain") {
      return Math.round(tdee)
    }

    if (goalWeight && goalDate) {
      const targetWeight = Number(goalWeight)
      const currentWeight = Number(weight)
      const weightDiff = targetWeight - currentWeight

      const totalCalorieChange = weightDiff * CALORIES_PER_KG_FAT

      const today = startOfToday()
      const daysRemaining = differenceInCalendarDays(goalDate, today)

      const safeDays = Math.max(daysRemaining, 1)

      dailyAdjustment = totalCalorieChange / safeDays
    } else {
      if (goalType === "lose") dailyAdjustment = -500
      if (goalType === "gain") dailyAdjustment = 300
      return Math.round(tdee + dailyAdjustment)
    }

    let targetCalories = tdee + dailyAdjustment

    if (targetCalories < MIN_CALORIES_SAFE_FLOOR) {
      targetCalories = MIN_CALORIES_SAFE_FLOOR
    }

    const MAX_SURPLUS = 2000
    if (dailyAdjustment > MAX_SURPLUS) targetCalories = tdee + MAX_SURPLUS

    return Math.round(targetCalories)
  }

  const handleNext = () => {
    setError("")
    if (currentStep === 1 && (!name || !surname || !age)) {
      setError("Please fill in all fields")
      return
    }
    if (currentStep === 2 && (!weight || !height)) {
      setError("Please fill in all fields")
      return
    }
    if (currentStep === 4 && (!goalWeight || !goalDate)) {
      setError("Please set your goal weight and target date")
      return
    }
    setCurrentStep((prev) => Math.min(prev + 1, 4))
  }

  const handleBack = () => {
    setCurrentStep((prev) => Math.max(prev - 1, 1))
  }

  const handleComplete = async () => {
    setError("")
    if (!goalWeight || !goalDate) {
      setError("Please set your goal weight and target date")
      return
    }

    setIsLoading(true)

    try {
      // Get registration credentials from sessionStorage
      const username = typeof window !== "undefined" ? sessionStorage.getItem("registration_username") : null
      const email = typeof window !== "undefined" ? sessionStorage.getItem("registration_email") : null
      const password = typeof window !== "undefined" ? sessionStorage.getItem("registration_password") : null

      if (!username || !email || !password) {
        setError("Registration session expired. Please start again.")
        setIsLoading(false)
        return
      }

      // Calculate recommended values
      const recommendedCalories = calculateRecommendedCalories()
      const calculatedProtein = Math.round(Number(weight) * 1.6)
      const calculatedCarbs = Math.round((recommendedCalories * 0.45) / 4)
      const calculatedFat = Math.round((recommendedCalories * 0.3) / 9)

      const formattedDate = format(goalDate, "yyyy-MM-dd")

      const { registerWithOnboarding } = await import("@/lib/auth")
      const result = await registerWithOnboarding({
        username,
        email,
        password,
        name,
        surname,
        age: Number(age),
        weight: Number(weight),
        height: Number(height),
        bmi: calculateBMI(),
        lifestyle,
        goalType,
        goalWeight: Number(goalWeight),
        goalDate: formattedDate,
        calorieLimit: recommendedCalories,
        proteinLimit: calculatedProtein,
        carbLimit: calculatedCarbs,
        fatLimit: calculatedFat,
        waterGoal: 2500,
      })

      if (result.success) {
        if (typeof window !== "undefined") {
          sessionStorage.removeItem("registration_username")
          sessionStorage.removeItem("registration_email")
          sessionStorage.removeItem("registration_password")
        }

        router.push(`/verify?email=${encodeURIComponent(email)}`)
      } else {
        setError(result.message || "Failed to complete registration. Please try again.")
      }
    } catch {
      setError("Failed to save your information. Please try again.")
    }

    setIsLoading(false)
  }

  return (
    <div className="w-full max-w-lg space-y-8">
      {/* Progress Steps */}
      <div className="flex items-center justify-between">
        {STEPS.map((step, index) => (
          <div key={step.id} className="flex items-center">
            <div
              className={`flex h-10 w-10 items-center justify-center rounded-full border-2 transition-colors ${
                currentStep >= step.id
                  ? "border-secondary bg-secondary text-secondary-foreground"
                  : "border-muted bg-background text-muted-foreground"
              }`}
            >
              {currentStep > step.id ? (
                <Check className="h-5 w-5" />
              ) : (
                <step.icon className="h-5 w-5" />
              )}
            </div>
            {index < STEPS.length - 1 && (
              <div
                className={`h-0.5 w-8 sm:w-12 ${
                  currentStep > step.id ? "bg-secondary" : "bg-muted"
                }`}
              />
            )}
          </div>
        ))}
      </div>

      {/* Header */}
      <div className="text-center">
        <h1 className="text-2xl font-bold text-foreground sm:text-3xl">
          {currentStep === 1 && "Tell us about yourself"}
          {currentStep === 2 && "Your body stats"}
          {currentStep === 3 && "Your lifestyle"}
          {currentStep === 4 && "Set your goal"}
        </h1>
        <p className="mt-2 text-muted-foreground">
          {currentStep === 1 && "This helps us personalize your experience"}
          {currentStep === 2 && "We'll use this to calculate your needs"}
          {currentStep === 3 && "Activity level affects your calorie needs"}
          {currentStep === 4 && "What do you want to achieve?"}
        </p>
      </div>

      {/* Error */}
      {error && (
        <div className="rounded-lg border border-destructive/50 bg-destructive/10 p-4 text-sm text-destructive">
          {error}
        </div>
      )}

      {/* Step 1: Personal Info */}
      {currentStep === 1 && (
        <div className="space-y-4">
          <div className="grid gap-4 sm:grid-cols-2">
            <div className="space-y-2">
              <Label htmlFor="name">First Name</Label>
              <Input
                id="name"
                type="text"
                placeholder="John"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="h-12"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="surname">Last Name</Label>
              <Input
                id="surname"
                type="text"
                placeholder="Doe"
                value={surname}
                onChange={(e) => setSurname(e.target.value)}
                className="h-12"
              />
            </div>
          </div>
          <div className="space-y-2">
            <Label htmlFor="age">Age</Label>
            <Input
              id="age"
              type="number"
              placeholder="25"
              value={age}
              onChange={(e) => setAge(e.target.value)}
              min={13}
              max={120}
              className="h-12"
            />
          </div>
        </div>
      )}

      {/* Step 2: Body Stats */}
      {currentStep === 2 && (
        <div className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="weight">Current Weight</Label>
            <div className="flex items-center gap-2">
              <Input
                id="weight"
                type="number"
                placeholder="70"
                value={weight}
                onChange={(e) => setWeight(e.target.value)}
                min={20}
                max={500}
                step="0.1"
                className="h-12"
              />
              <span className="text-muted-foreground">kg</span>
            </div>
          </div>
          <div className="space-y-2">
            <Label htmlFor="height">Height</Label>
            <div className="flex items-center gap-2">
              <Input
                id="height"
                type="number"
                placeholder="175"
                value={height}
                onChange={(e) => setHeight(e.target.value)}
                min={100}
                max={250}
                className="h-12"
              />
              <span className="text-muted-foreground">cm</span>
            </div>
          </div>
          {weight && height && (
            <div className="rounded-lg bg-muted/50 p-4">
              <p className="text-sm text-muted-foreground">Your BMI</p>
              <p className="text-2xl font-bold text-foreground">{calculateBMI()}</p>
              <p className="mt-1 text-xs text-muted-foreground">
                {calculateBMI() < 18.5 && "Underweight"}
                {calculateBMI() >= 18.5 && calculateBMI() < 25 && "Normal weight"}
                {calculateBMI() >= 25 && calculateBMI() < 30 && "Overweight"}
                {calculateBMI() >= 30 && "Obese"}
              </p>
            </div>
          )}
        </div>
      )}

      {/* Step 3: Lifestyle */}
      {currentStep === 3 && (
        <RadioGroup value={lifestyle} onValueChange={setLifestyle} className="space-y-3">
          {LIFESTYLE_OPTIONS.map((option) => (
            <div key={option.value}>
              <RadioGroupItem
                value={option.value}
                id={option.value}
                className="peer sr-only"
              />
              <Label
                htmlFor={option.value}
                className="flex cursor-pointer items-center gap-4 rounded-lg border-2 border-muted bg-card p-4 transition-colors hover:bg-muted/50 peer-data-[state=checked]:border-secondary peer-data-[state=checked]:bg-secondary/10"
              >
                <div className="flex h-10 w-10 items-center justify-center rounded-full bg-muted">
                  <Activity className="h-5 w-5 text-muted-foreground" />
                </div>
                <div>
                  <p className="font-medium text-foreground">{option.label}</p>
                  <p className="text-sm text-muted-foreground">{option.description}</p>
                </div>
              </Label>
            </div>
          ))}
        </RadioGroup>
      )}

      {/* Step 4: Goals */}
      {currentStep === 4 && (
        <div className="space-y-6">
          <RadioGroup value={goalType} onValueChange={setGoalType} className="space-y-3">
            {GOAL_OPTIONS.map((option) => (
              <div key={option.value}>
                <RadioGroupItem
                  value={option.value}
                  id={option.value}
                  className="peer sr-only"
                />
                <Label
                  htmlFor={option.value}
                  className="flex cursor-pointer items-center gap-4 rounded-lg border-2 border-muted bg-card p-4 transition-colors hover:bg-muted/50 peer-data-[state=checked]:border-secondary peer-data-[state=checked]:bg-secondary/10"
                >
                  <div className="flex h-10 w-10 items-center justify-center rounded-full bg-muted">
                    <Target className="h-5 w-5 text-muted-foreground" />
                  </div>
                  <div>
                    <p className="font-medium text-foreground">{option.label}</p>
                    <p className="text-sm text-muted-foreground">{option.description}</p>
                  </div>
                </Label>
              </div>
            ))}
          </RadioGroup>

          <div className="grid gap-4 sm:grid-cols-2">
            <div className="space-y-2">
              <Label htmlFor="goalWeight">Target Weight</Label>
              <div className="flex items-center gap-2">
                <Input
                  id="goalWeight"
                  type="number"
                  placeholder={weight || "65"}
                  value={goalWeight}
                  onChange={(e) => setGoalWeight(e.target.value)}
                  min={20}
                  max={500}
                  step="0.1"
                  className="h-12"
                />
                <span className="text-muted-foreground">kg</span>
              </div>
            </div>
            <div className="space-y-2 flex flex-col">
              <Label htmlFor="goalDate" className="mb-2">Target Date</Label>
              <Popover>
                <PopoverTrigger asChild>
                  <Button
                    variant="outline"
                    className={cn(
                      "h-12 w-full justify-start text-left font-normal",
                      !goalDate && "text-muted-foreground"
                    )}
                  >
                    <CalendarIcon className="mr-2 h-4 w-4" />
                    {goalDate ? format(goalDate, "EEE, MMM d, yyyy") : <span>Pick a date</span>}
                  </Button>
                </PopoverTrigger>
                <PopoverContent className="w-auto p-0" align="end">
                  <Calendar
                    mode="single"
                    selected={goalDate}
                    onSelect={setGoalDate}
                    disabled={(date) => date < new Date()}
                    initialFocus
                  />
                </PopoverContent>
              </Popover>
            </div>
          </div>

          {weight && goalWeight && (
            <div className="rounded-lg bg-secondary/10 p-4">
              <div className="flex items-center gap-2">
                <Sparkles className="h-5 w-5 text-secondary" />
                <p className="font-medium text-secondary">Recommended Daily Intake</p>
              </div>
              <p className="mt-2 text-2xl font-bold text-foreground">
                {calculateRecommendedCalories()} kcal
              </p>
              <p className="text-sm text-muted-foreground">
                Based on your stats and goal
              </p>
            </div>
          )}
        </div>
      )}

      {/* Navigation */}
      <div className="flex gap-3">
        {currentStep > 1 && (
          <Button
            type="button"
            variant="outline"
            onClick={handleBack}
            className="flex-1 h-12 bg-transparent"
          >
            <ArrowLeft className="mr-2 h-4 w-4" />
            Back
          </Button>
        )}
        {currentStep < 4 ? (
          <Button
            type="button"
            onClick={handleNext}
            className="flex-1 h-12 bg-primary text-primary-foreground hover:bg-primary/90"
          >
            Continue
            <ArrowRight className="ml-2 h-4 w-4" />
          </Button>
        ) : (
          <Button
            type="button"
            onClick={handleComplete}
            disabled={isLoading}
            className="flex-1 h-12 bg-secondary text-secondary-foreground hover:bg-secondary/90"
          >
            {isLoading ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Setting up...
              </>
            ) : (
              <>
                <Sparkles className="mr-2 h-4 w-4" />
                Start Your Journey
              </>
            )}
          </Button>
        )}
      </div>
    </div>
  )
}
