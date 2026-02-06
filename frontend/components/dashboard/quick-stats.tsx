"use client"

import { TrendingUp, TrendingDown, Minus, Scale, Flame, Target } from "lucide-react"
import { cn } from "@/lib/utils"

interface QuickStatsProps {
  currentWeight?: number
  weightChange?: number
  streak?: number
  weeklyAvgCalories?: number
  calorieGoal?: number
  bmi?: number
}

export function QuickStats({
  currentWeight,
  weightChange = 0,
  streak = 0,
  weeklyAvgCalories = 0,
  calorieGoal = 2000,
  bmi,
}: QuickStatsProps) {
  const weightTrend = weightChange > 0 ? "up" : weightChange < 0 ? "down" : "neutral"
  const WeightIcon = weightTrend === "up" ? TrendingUp : weightTrend === "down" ? TrendingDown : Minus

  return (
    <div className="grid grid-cols-2 gap-3 sm:grid-cols-5">
      <div className="rounded-xl border border-border bg-card p-4">
        <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary/20">
          <Scale className="h-5 w-5 text-primary" />
        </div>
        <p className="mt-3 text-2xl font-bold text-foreground">
          {currentWeight ? `${currentWeight}kg` : "--"}
        </p>
        <div className="mt-1 flex items-center gap-1">
          <WeightIcon
            className={cn(
              "h-3.5 w-3.5",
              weightTrend === "up" && "text-accent",
              weightTrend === "down" && "text-secondary",
              weightTrend === "neutral" && "text-muted-foreground"
            )}
          />
          <span className="text-xs text-muted-foreground">
            {weightChange !== 0 ? `${weightChange > 0 ? "+" : ""}${weightChange}kg` : "No change"}
          </span>
        </div>
      </div>

      <div className="rounded-xl border border-border bg-card p-4">
        <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-accent/20">
          <Scale className="h-5 w-5 text-accent" />
        </div>
        <p className="mt-3 text-2xl font-bold text-foreground">
          {bmi ? bmi : "--"}
        </p>
        <p className="mt-1 text-xs text-muted-foreground">BMI</p>
      </div>

      <div className="rounded-xl border border-border bg-card p-4">
        <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-accent/20">
          <Flame className="h-5 w-5 text-accent" />
        </div>
        <p className="mt-3 text-2xl font-bold text-foreground">{streak}</p>
        <p className="mt-1 text-xs text-muted-foreground">Day streak</p>
      </div>

      <div className="rounded-xl border border-border bg-card p-4">
        <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-secondary/20">
          <Target className="h-5 w-5 text-secondary" />
        </div>
        <p className="mt-3 text-2xl font-bold text-foreground">{Math.round(weeklyAvgCalories)}</p>
        <p className="mt-1 text-xs text-muted-foreground">Avg. daily kcal</p>
      </div>

      <div className="rounded-xl border border-border bg-card p-4">
        <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-muted">
          <Target className="h-5 w-5 text-muted-foreground" />
        </div>
        <p className="mt-3 text-2xl font-bold text-foreground">{calorieGoal}</p>
        <p className="mt-1 text-xs text-muted-foreground">Daily goal</p>
      </div>
    </div>
  )
}
