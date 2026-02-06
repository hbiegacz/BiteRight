"use client"

import { cn } from "@/lib/utils"

interface CalorieRingProps {
  consumed: number
  limit: number
  size?: number
  strokeWidth?: number
}

export function CalorieRing({
  consumed,
  limit,
  size = 280,
  strokeWidth = 20,
}: CalorieRingProps) {
  const radius = (size - strokeWidth) / 2
  const circumference = radius * 2 * Math.PI
  const percentage = Math.min((consumed / limit) * 100, 100)
  const offset = circumference - (percentage / 100) * circumference
  const isOver = consumed > limit
  const difference = Math.abs(consumed - limit)

  return (
    <div className="relative flex items-center justify-center" style={{ width: size, height: size }}>
      <svg
        width={size}
        height={size}
        className="-rotate-90"
      >
        {/* Background circle */}
        <circle
          cx={size / 2}
          cy={size / 2}
          r={radius}
          fill="none"
          stroke="currentColor"
          strokeWidth={strokeWidth}
          className="text-muted/50"
        />
        {/* Progress circle */}
        <circle
          cx={size / 2}
          cy={size / 2}
          r={radius}
          fill="none"
          stroke="currentColor"
          strokeWidth={strokeWidth}
          strokeDasharray={circumference}
          strokeDashoffset={offset}
          strokeLinecap="round"
          className={cn(
            "transition-all duration-700 ease-out",
            isOver ? "text-destructive" : "text-secondary"
          )}
        />
      </svg>
      <div className="absolute flex flex-col items-center justify-center text-center">
        <span className={cn("text-5xl font-bold", isOver ? "text-destructive" : "text-foreground")}>
          {Math.round(difference).toLocaleString()}
        </span>
        <span className="mt-1 text-sm font-medium text-muted-foreground">
          {isOver ? "kcal over limit" : "kcal remaining"}
        </span>
        <div className="mt-3 flex items-baseline gap-1.5">
          <span className={cn("text-lg font-semibold", isOver ? "text-destructive" : "text-foreground")}>
            {consumed.toLocaleString()}
          </span>
          <span className="text-sm text-muted-foreground">/ {limit.toLocaleString()} kcal</span>
        </div>
      </div>
    </div>
  )
}
