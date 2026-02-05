"use client"

import { cn } from "@/lib/utils"

interface MacroCardProps {
  label: string
  value: number
  limit: number
  unit: string
  color: "protein" | "carbs" | "fat"
}

const colorMap = {
  protein: {
    bg: "bg-secondary/20",
    bar: "bg-secondary",
    text: "text-secondary",
  },
  carbs: {
    bg: "bg-accent/20",
    bar: "bg-accent",
    text: "text-accent",
  },
  fat: {
    bg: "bg-primary/20",
    bar: "bg-primary",
    text: "text-primary",
  },
}

export function MacroCard({ label, value, limit, unit, color }: MacroCardProps) {
  const percentage = Math.min((value / limit) * 100, 100)
  const colors = colorMap[color]

  return (
    <div className="rounded-xl border border-border bg-card p-4">
      <div className="flex items-center justify-between">
        <span className="text-sm font-medium text-muted-foreground">{label}</span>
        <span className={cn("text-xs font-medium", colors.text)}>
          {Math.round(percentage)}%
        </span>
      </div>
      <div className="mt-2 flex items-baseline gap-1">
        <span className="text-2xl font-bold text-foreground">{Math.round(value)}</span>
        <span className="text-sm text-muted-foreground">/ {limit}{unit}</span>
      </div>
      <div className={cn("mt-3 h-2 w-full overflow-hidden rounded-full", colors.bg)}>
        <div
          className={cn("h-full rounded-full transition-all duration-500", colors.bar)}
          style={{ width: `${percentage}%` }}
        />
      </div>
    </div>
  )
}
