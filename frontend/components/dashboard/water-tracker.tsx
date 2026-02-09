"use client"

import { Droplets, Plus } from "lucide-react"
import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"

interface WaterTrackerProps {
  consumed: number
  goal: number
  onAddWater?: (amount: number) => void
}

export function WaterTracker({ consumed, goal, onAddWater }: WaterTrackerProps) {
  const percentage = Math.min((consumed / goal) * 100, 100)
  const glasses = Math.floor(consumed / 250) // 250ml per glass
  const isOverGoal = consumed > goal

  return (
    <div className="rounded-xl border border-border bg-card p-5">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-blue-500/20">
            <Droplets className="h-5 w-5 text-blue-500" />
          </div>
          <div>
            <h3 className="font-semibold text-foreground">Water Intake</h3>
            <p className="text-sm text-muted-foreground">{glasses} glasses today</p>
          </div>
        </div>
        {onAddWater && (
          <Button
            variant="outline"
            size="sm"
            onClick={() => onAddWater(250)}
            className="gap-1"
          >
            <Plus className="h-4 w-4" />
            250ml
          </Button>
        )}
      </div>

      <div className="mt-4">
        <div className="flex items-baseline justify-between">
          <span className="text-2xl font-bold text-foreground">
            {(consumed / 1000).toFixed(1)}L
          </span>
          <span className="text-sm text-muted-foreground">/ {(goal / 1000).toFixed(1)}L goal</span>
        </div>
        <div className={cn("mt-2 h-3 w-full overflow-hidden rounded-full", isOverGoal ? "bg-destructive/20" : "bg-blue-500/20")}>
          <div
            className={cn(
              "h-full rounded-full transition-all duration-500",
              isOverGoal ? "bg-destructive" : "bg-blue-500"
            )}
            style={{ width: `${percentage}%` }}
          />
        </div>
      </div>
    </div>
  )
}
