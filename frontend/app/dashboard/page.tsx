"use client"

import { useEffect, useState, useCallback } from "react"
import Link from "next/link"
import { format } from "date-fns"
import { CalorieRing } from "@/components/dashboard/calorie-ring"
import { MacroCard } from "@/components/dashboard/macro-card"
import { MealList } from "@/components/dashboard/meal-list"
import { WaterTracker } from "@/components/dashboard/water-tracker"
import { QuickStats } from "@/components/dashboard/quick-stats"
import { Button } from "@/components/ui/button"
import { Calendar } from "@/components/ui/calendar"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import {
  getDailyLimits,
  getMealsByDate,
  getWaterIntakeByDate,
  getLastWeight,
  addWaterIntake,
  searchIngredients,
  type DailyLimits,
  type Meal,
  type WaterIntake,
  type Ingredient,
} from "@/lib/api"
import { CalendarIcon, Plus, ChevronLeft, ChevronRight } from "lucide-react"
import { cn } from "@/lib/utils"

// Default limits if user hasn't set any
const DEFAULT_LIMITS: DailyLimits = {
  calorieLimit: 2000,
  proteinLimit: 150,
  fatLimit: 65,
  carbLimit: 250,
  waterGoal: 2500,
}

export default function DashboardPage() {
  const [selectedDate, setSelectedDate] = useState<Date>(new Date())
  const [limits, setLimits] = useState<DailyLimits>(DEFAULT_LIMITS)
  const [meals, setMeals] = useState<Meal[]>([])
  const [waterIntakes, setWaterIntakes] = useState<WaterIntake[]>([])
  const [ingredients, setIngredients] = useState<Map<number, Ingredient>>(new Map())
  const [currentWeight, setCurrentWeight] = useState<number | undefined>()
  const [loading, setLoading] = useState(true)

  const dateString = format(selectedDate, "yyyy-MM-dd")

  // Calculate totals from meals
  const calculateNutrition = useCallback(() => {
    let totalCalories = 0
    let totalProtein = 0
    let totalCarbs = 0
    let totalFat = 0

    for (const meal of meals) {
      for (const content of meal.contents) {
        const ingredient = ingredients.get(content.ingredientId)
        if (ingredient) {
          const multiplier = content.ingredientAmount / ingredient.portionSize
          totalCalories += ingredient.calories * multiplier
          totalProtein += ingredient.protein * multiplier
          totalCarbs += ingredient.carbs * multiplier
          totalFat += ingredient.fat * multiplier
        }
      }
    }

    return { totalCalories, totalProtein, totalCarbs, totalFat }
  }, [meals, ingredients])

  const totalWater = waterIntakes.reduce((sum, w) => sum + w.waterAmount, 0)
  const nutrition = calculateNutrition()

  // Load data
  useEffect(() => {
    async function loadData() {
      setLoading(true)

      // Load limits
      const userLimits = await getDailyLimits()
      if (userLimits) {
        setLimits(userLimits)
      }

      // Load meals for selected date
      const dayMeals = await getMealsByDate(dateString)
      setMeals(dayMeals)

      // Load ingredient details for all meals
      const ingredientIds = new Set<number>()
      for (const meal of dayMeals) {
        for (const content of meal.contents) {
          ingredientIds.add(content.ingredientId)
        }
      }

      // Fetch ingredient details (in real app, you'd have a batch endpoint)
      const ingredientMap = new Map<number, Ingredient>()
      for (const meal of dayMeals) {
        for (const content of meal.contents) {
          if (!ingredientMap.has(content.ingredientId)) {
            // Search by name to get full ingredient data
            const results = await searchIngredients(content.ingredientName)
            const found = results.find((i) => i.id === content.ingredientId)
            if (found) {
              ingredientMap.set(content.ingredientId, found)
            }
          }
        }
      }
      setIngredients(ingredientMap)

      // Load water intake for selected date
      const dayWater = await getWaterIntakeByDate(dateString)
      setWaterIntakes(dayWater)

      // Load latest weight
      const lastWeight = await getLastWeight()
      if (lastWeight) {
        setCurrentWeight(lastWeight.weight)
      }

      setLoading(false)
    }

    loadData()
  }, [dateString])

  const handleAddWater = async (amount: number) => {
    try {
      const result = await addWaterIntake(amount, dateString)
      if (result) {
        setWaterIntakes((prev) => [...prev, result])
      }
    } catch (error) {
      console.error("Error adding water intake:", error)
    }
  }

  const goToPreviousDay = () => {
    try {
      setSelectedDate((prev) => {
        const newDate = new Date(prev)
        newDate.setDate(newDate.getDate() - 1)
        return newDate
      })
    } catch (error) {
      console.error("Error going to previous day:", error)
    }
  }

  const goToNextDay = () => {
    try {
      setSelectedDate((prev) => {
        const newDate = new Date(prev)
        newDate.setDate(newDate.getDate() + 1)
        return newDate
      })
    } catch (error) {
      console.error("Error going to next day:", error)
    }
  }

  const isToday = format(selectedDate, "yyyy-MM-dd") === format(new Date(), "yyyy-MM-dd")

  return (
    <div className="mx-auto max-w-7xl space-y-6">
      {/* Header with date picker */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-foreground sm:text-3xl">Dashboard</h1>
          <p className="mt-1 text-muted-foreground">Track your daily nutrition and progress</p>
        </div>

        <div className="flex items-center gap-2">
          <Button variant="outline" size="icon" onClick={goToPreviousDay}>
            <ChevronLeft className="h-4 w-4" />
          </Button>

          <Popover>
            <PopoverTrigger asChild>
              <Button
                variant="outline"
                className={cn(
                  "min-w-[200px] justify-start text-left font-normal",
                  !selectedDate && "text-muted-foreground"
                )}
              >
                <CalendarIcon className="mr-2 h-4 w-4" />
                {isToday ? "Today" : format(selectedDate, "EEE, MMM d, yyyy")}
              </Button>
            </PopoverTrigger>
            <PopoverContent className="w-auto p-0" align="end">
              <Calendar
                mode="single"
                selected={selectedDate}
                onSelect={(date) => date && setSelectedDate(date)}
                initialFocus
              />
            </PopoverContent>
          </Popover>

          <Button variant="outline" size="icon" onClick={goToNextDay} disabled={isToday}>
            <ChevronRight className="h-4 w-4" />
          </Button>
        </div>
      </div>

      {/* Quick Stats */}
      <QuickStats
        currentWeight={currentWeight}
        calorieGoal={limits.calorieLimit}
      />

      {/* Main content grid */}
      <div className="grid gap-6 lg:grid-cols-3">
        {/* Calorie Ring - Center focus */}
        <div className="lg:col-span-2">
          <div className="rounded-2xl border border-border bg-card p-6">
            <div className="flex flex-col items-center gap-8 md:flex-row md:justify-around">
              {/* Calorie Ring */}
              <div className="flex flex-col items-center">
                <CalorieRing
                  consumed={Math.round(nutrition.totalCalories)}
                  limit={limits.calorieLimit}
                />
              </div>

              {/* Macros */}
              <div className="grid w-full max-w-sm gap-4">
                <MacroCard
                  label="Protein"
                  value={nutrition.totalProtein}
                  limit={limits.proteinLimit}
                  unit="g"
                  color="protein"
                />
                <MacroCard
                  label="Carbohydrates"
                  value={nutrition.totalCarbs}
                  limit={limits.carbLimit}
                  unit="g"
                  color="carbs"
                />
                <MacroCard
                  label="Fat"
                  value={nutrition.totalFat}
                  limit={limits.fatLimit}
                  unit="g"
                  color="fat"
                />
              </div>
            </div>
          </div>
        </div>

        {/* Water Tracker */}
        <div className="space-y-6">
          <WaterTracker
            consumed={totalWater}
            goal={limits.waterGoal}
            onAddWater={handleAddWater}
          />

          {/* Quick Actions */}
          <div className="rounded-xl border border-border bg-card p-5">
            <h3 className="font-semibold text-foreground">Quick Actions</h3>
            <div className="mt-4 grid gap-2">
              <Button asChild className="justify-start gap-2">
                <Link href="/dashboard/meals">
                  <Plus className="h-4 w-4" />
                  Log a Meal
                </Link>
              </Button>
              <Button variant="outline" asChild className="justify-start gap-2 bg-transparent">
                <Link href="/dashboard/exercises">
                  <Plus className="h-4 w-4" />
                  Log Exercise
                </Link>
              </Button>
              <Button variant="outline" asChild className="justify-start gap-2 bg-transparent">
                <Link href="/dashboard/recipes">
                  <Plus className="h-4 w-4" />
                  Find Recipes
                </Link>
              </Button>
            </div>
          </div>
        </div>
      </div>

      {/* Today's Meals */}
      <div className="rounded-2xl border border-border bg-card p-6">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-foreground">
            {isToday ? "Today's Meals" : `Meals for ${format(selectedDate, "MMM d")}`}
          </h2>
          <Button asChild size="sm" variant="outline">
            <Link href="/dashboard/meals">View All</Link>
          </Button>
        </div>
        <div className="mt-4">
          {loading ? (
            <div className="flex items-center justify-center py-12">
              <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
          ) : (
            <MealList meals={meals} ingredients={ingredients} />
          )}
        </div>
      </div>
    </div>
  )
}
