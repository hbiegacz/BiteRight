"use client"

import { Utensils, Coffee, Sun, Moon, Apple } from "lucide-react"
import type { Meal, Ingredient } from "@/lib/api"

interface MealListProps {
  meals: Meal[]
  ingredients: Map<number, Ingredient>
}

const mealTypeIcons: Record<string, typeof Utensils> = {
  Breakfast: Coffee,
  Lunch: Sun,
  Dinner: Moon,
  Snack: Apple,
}

function getMealNutrition(meal: Meal, ingredients: Map<number, Ingredient>) {
  let calories = 0
  let protein = 0
  let carbs = 0
  let fat = 0

  for (const content of meal.contents) {
    const ingredient = ingredients.get(content.ingredientId)
    if (ingredient) {
      const multiplier = content.ingredientAmount / ingredient.portionSize
      calories += ingredient.calories * multiplier
      protein += ingredient.protein * multiplier
      carbs += ingredient.carbs * multiplier
      fat += ingredient.fat * multiplier
    }
  }

  return { calories, protein, carbs, fat }
}

export function MealList({ meals, ingredients }: MealListProps) {
  if (meals.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center py-12 text-center">
        <div className="flex h-16 w-16 items-center justify-center rounded-full bg-muted">
          <Utensils className="h-8 w-8 text-muted-foreground" />
        </div>
        <p className="mt-4 text-sm font-medium text-foreground">No meals logged today</p>
        <p className="mt-1 text-sm text-muted-foreground">Start tracking by adding your first meal</p>
      </div>
    )
  }

  return (
    <div className="space-y-3">
      {meals.map((meal) => {
        const Icon = mealTypeIcons[meal.mealTypeName] || Utensils
        const nutrition = getMealNutrition(meal, ingredients)

        return (
          <div
            key={meal.mealId}
            className="flex items-center gap-4 rounded-xl border border-border bg-card/50 p-4 transition-colors hover:bg-card"
          >
            <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-lg bg-secondary/20">
              <Icon className="h-6 w-6 text-secondary" />
            </div>
            <div className="min-w-0 flex-1">
              <div className="flex items-center gap-2">
                <h4 className="truncate font-medium text-foreground">{meal.name}</h4>
                <span className="shrink-0 rounded-full bg-muted px-2 py-0.5 text-xs text-muted-foreground">
                  {meal.mealTypeName}
                </span>
              </div>
              <p className="mt-0.5 text-sm text-muted-foreground">
                {meal.contents.length} item{meal.contents.length !== 1 ? "s" : ""}
              </p>
            </div>
            <div className="text-right">
              <p className="font-semibold text-foreground">{Math.round(nutrition.calories)} kcal</p>
              <div className="mt-0.5 flex gap-2 text-xs text-muted-foreground">
                <span>P: {Math.round(nutrition.protein)}g</span>
                <span>C: {Math.round(nutrition.carbs)}g</span>
                <span>F: {Math.round(nutrition.fat)}g</span>
              </div>
            </div>
          </div>
        )
      })}
    </div>
  )
}
