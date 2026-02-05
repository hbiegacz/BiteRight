"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { TrendingUp, TrendingDown, Scale, Flame, Droplets, Target } from "lucide-react"

// Placeholder data - would come from API in production
const weeklyData = [
  { day: "Mon", calories: 1850, protein: 120, weight: 75.2 },
  { day: "Tue", calories: 2100, protein: 145, weight: 75.0 },
  { day: "Wed", calories: 1920, protein: 130, weight: 74.8 },
  { day: "Thu", calories: 2050, protein: 140, weight: 74.9 },
  { day: "Fri", calories: 1780, protein: 115, weight: 74.7 },
  { day: "Sat", calories: 2200, protein: 150, weight: 74.5 },
  { day: "Sun", calories: 1950, protein: 135, weight: 74.6 },
]

export default function ProgressPage() {
  const [timeRange, setTimeRange] = useState("week")

  const avgCalories = Math.round(
    weeklyData.reduce((sum, d) => sum + d.calories, 0) / weeklyData.length
  )
  const avgProtein = Math.round(
    weeklyData.reduce((sum, d) => sum + d.protein, 0) / weeklyData.length
  )
  const startWeight = weeklyData[0].weight
  const endWeight = weeklyData[weeklyData.length - 1].weight
  const weightChange = (endWeight - startWeight).toFixed(1)

  return (
    <div className="mx-auto max-w-6xl space-y-6">
      {/* Header */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-foreground sm:text-3xl">Progress</h1>
          <p className="mt-1 text-muted-foreground">Track your health journey over time</p>
        </div>

        <Select value={timeRange} onValueChange={setTimeRange}>
          <SelectTrigger className="w-[140px]">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="week">This Week</SelectItem>
            <SelectItem value="month">This Month</SelectItem>
            <SelectItem value="3months">3 Months</SelectItem>
            <SelectItem value="year">This Year</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* Summary Stats */}
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <Card>
          <CardContent className="p-4">
            <div className="flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-secondary/20">
                <Flame className="h-5 w-5 text-secondary" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Avg. Calories</p>
                <p className="text-xl font-bold text-foreground">{avgCalories} kcal</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-4">
            <div className="flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-accent/20">
                <Target className="h-5 w-5 text-accent" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Avg. Protein</p>
                <p className="text-xl font-bold text-foreground">{avgProtein}g</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-4">
            <div className="flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/20">
                <Scale className="h-5 w-5 text-primary" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Current Weight</p>
                <p className="text-xl font-bold text-foreground">{endWeight}kg</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-4">
            <div className="flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-blue-500/20">
                {Number(weightChange) < 0 ? (
                  <TrendingDown className="h-5 w-5 text-secondary" />
                ) : (
                  <TrendingUp className="h-5 w-5 text-accent" />
                )}
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Weight Change</p>
                <p className="text-xl font-bold text-foreground">
                  {Number(weightChange) > 0 ? "+" : ""}
                  {weightChange}kg
                </p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Charts */}
      <div className="grid gap-6 lg:grid-cols-2">
        {/* Calorie Chart */}
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Calorie Intake</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {weeklyData.map((day) => {
                const percentage = Math.min((day.calories / 2200) * 100, 100)
                return (
                  <div key={day.day} className="flex items-center gap-3">
                    <span className="w-8 text-sm font-medium text-muted-foreground">
                      {day.day}
                    </span>
                    <div className="h-6 flex-1 overflow-hidden rounded-full bg-muted">
                      <div
                        className="h-full rounded-full bg-secondary transition-all"
                        style={{ width: `${percentage}%` }}
                      />
                    </div>
                    <span className="w-16 text-right text-sm font-medium text-foreground">
                      {day.calories}
                    </span>
                  </div>
                )
              })}
            </div>
            <div className="mt-4 flex items-center justify-between text-xs text-muted-foreground">
              <span>0 kcal</span>
              <span>Goal: 2000 kcal</span>
              <span>2200 kcal</span>
            </div>
          </CardContent>
        </Card>

        {/* Weight Chart */}
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Weight Trend</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="relative h-48">
              {/* Simple line visualization */}
              <svg className="h-full w-full" viewBox="0 0 300 150">
                {/* Grid lines */}
                {[0, 1, 2, 3, 4].map((i) => (
                  <line
                    key={i}
                    x1="0"
                    y1={i * 37.5}
                    x2="300"
                    y2={i * 37.5}
                    stroke="currentColor"
                    strokeWidth="1"
                    className="text-muted/30"
                  />
                ))}
                {/* Weight line */}
                <polyline
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="3"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  className="text-primary"
                  points={weeklyData
                    .map((d, i) => {
                      const x = (i / (weeklyData.length - 1)) * 280 + 10
                      const y = 150 - ((d.weight - 74) / 2) * 150
                      return `${x},${y}`
                    })
                    .join(" ")}
                />
                {/* Data points */}
                {weeklyData.map((d, i) => {
                  const x = (i / (weeklyData.length - 1)) * 280 + 10
                  const y = 150 - ((d.weight - 74) / 2) * 150
                  return (
                    <circle
                      key={d.day}
                      cx={x}
                      cy={y}
                      r="5"
                      fill="currentColor"
                      className="text-primary"
                    />
                  )
                })}
              </svg>
            </div>
            <div className="mt-2 flex justify-between text-xs text-muted-foreground">
              {weeklyData.map((d) => (
                <span key={d.day}>{d.day}</span>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Detailed History */}
      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Daily Breakdown</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-border text-left text-sm text-muted-foreground">
                  <th className="pb-3 font-medium">Day</th>
                  <th className="pb-3 font-medium">Calories</th>
                  <th className="pb-3 font-medium">Protein</th>
                  <th className="pb-3 font-medium">Weight</th>
                  <th className="pb-3 font-medium">Status</th>
                </tr>
              </thead>
              <tbody>
                {weeklyData.map((day, index) => {
                  const onTarget = day.calories >= 1800 && day.calories <= 2200
                  const prevWeight = index > 0 ? weeklyData[index - 1].weight : day.weight
                  const weightDiff = day.weight - prevWeight

                  return (
                    <tr key={day.day} className="border-b border-border last:border-0">
                      <td className="py-3 font-medium text-foreground">{day.day}</td>
                      <td className="py-3 text-foreground">{day.calories} kcal</td>
                      <td className="py-3 text-foreground">{day.protein}g</td>
                      <td className="py-3 text-foreground">
                        {day.weight}kg
                        {weightDiff !== 0 && (
                          <span
                            className={
                              weightDiff < 0 ? "ml-2 text-xs text-secondary" : "ml-2 text-xs text-accent"
                            }
                          >
                            ({weightDiff > 0 ? "+" : ""}
                            {weightDiff.toFixed(1)})
                          </span>
                        )}
                      </td>
                      <td className="py-3">
                        <span
                          className={`inline-flex rounded-full px-2 py-0.5 text-xs font-medium ${
                            onTarget
                              ? "bg-secondary/20 text-secondary"
                              : "bg-accent/20 text-accent"
                          }`}
                        >
                          {onTarget ? "On Target" : day.calories < 1800 ? "Under" : "Over"}
                        </span>
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
