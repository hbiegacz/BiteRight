"use client"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { TrendingUp, TrendingDown, Scale, Flame, Droplets, Target, Loader2 } from "lucide-react"
import { getToken } from "@/lib/auth"
import { format, subDays, isSameDay, parseISO, startOfDay } from "date-fns"

// Types matching backend response
interface UserInfo {
  id: number
  name: string
  surname: string
  age: number
  weight: number
  height: number
  lifestyle: string
  bmi: number
}

interface WeightHistory {
  id: number
  measurementDate: string
  weight: number
}



// Redefining types based on what we'll use from `getSummaryRange`
interface DailySummary {
  date: string
  totalCalories: number
  totalProtein: number
  totalFat: number
  totalCarbs: number
  totalWater: number
}

// We need to extend the type to include the date for our local processing
interface DayData extends DailySummary {
  day: string // "Mon", "Tue" etc
  fullDate: string // YYYY-MM-DD
  weight: number
}

const API_BASE_URL = (process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080").replace(/\/$/, "")

async function myAuthFetch(endpoint: string) {
  const token = getToken()
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
  }
  if (token) headers["Authorization"] = `Bearer ${token}`

  const res = await fetch(`${API_BASE_URL}${endpoint}`, { headers })
  if (!res.ok) throw new Error("Failed to fetch")
  return res.json()
}

export default function ProgressPage() {
  const [timeRange, setTimeRange] = useState("week") 
  const [data, setData] = useState<DayData[]>([])
  const [userInfo, setUserInfo] = useState<UserInfo | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    async function fetchData() {
      try {
        setLoading(true)

        const userInfoData = await myAuthFetch("/userInfo/findUserInfo")
        setUserInfo(userInfoData)

        const weightHistoryRes = await myAuthFetch("/weightHistory/findWeightHistoriesForUser?size=100&sortDir=asc&sortBy=measurementDate")
        const weightHistory: WeightHistory[] = weightHistoryRes.content || []

        const daysToFetch = 7
        const dates: Date[] = []
        for (let i = daysToFetch - 1; i >= 0; i--) {
          dates.push(subDays(new Date(), i))
        }

        const summaryPromises = dates.map(async (date) => {
          const dateStr = format(date, "yyyy-MM-dd")
          try {
            const summary = await myAuthFetch(`/dailySummary/find?date=${dateStr}`).catch(() => null)

            let weight = userInfoData.weight // Default to current

            const exactWeight = weightHistory.find(w => w.measurementDate === dateStr)
            if (exactWeight) {
              weight = exactWeight.weight
            } else {
              const prevWeights = weightHistory.filter(w => w.measurementDate <= dateStr)
              if (prevWeights.length > 0) {
                weight = prevWeights[prevWeights.length - 1].weight
              }
            }

            return {
              date: dateStr,
              day: format(date, "EEE"),
              fullDate: dateStr,
              weight: weight,
              totalCalories: summary?.totalCalories || 0,
              totalProtein: summary?.totalProtein || 0,
              totalFat: summary?.totalFat || 0,
              totalCarbs: summary?.totalCarbs || 0,
              totalWater: summary?.totalWater || 0
            } as DayData
          } catch (e) {
            return {
              date: dateStr,
              day: format(date, "EEE"),
              fullDate: dateStr,
              weight: userInfoData.weight,
              totalCalories: 0,
              totalProtein: 0,
              totalFat: 0,
              totalCarbs: 0,
              totalWater: 0
            } as DayData
          }
        })

        const weeklyData = await Promise.all(summaryPromises)
        setData(weeklyData)

      } catch (err) {
        console.error("Error fetching progress data:", err)
        setError("Failed to load progress data")
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [timeRange]) 

  const avgCalories = data.length > 0
    ? Math.round(data.reduce((sum, d) => sum + d.totalCalories, 0) / data.length)
    : 0

  const avgProtein = data.length > 0
    ? Math.round(data.reduce((sum, d) => sum + d.totalProtein, 0) / data.length)
    : 0

  const startWeight = data.length > 0 ? data[0].weight : (userInfo?.weight || 0)
  const endWeight = data.length > 0 ? data[data.length - 1].weight : (userInfo?.weight || 0)
  const weightChange = (endWeight - startWeight).toFixed(1)

  if (loading) {
    return (
      <div className="flex h-[50vh] w-full items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    )
  }

  if (error) {
    return (
      <div className="flex h-[50vh] w-full flex-col items-center justify-center gap-4">
        <p className="text-destructive">{error}</p>
        <Button onClick={() => window.location.reload()}>Retry</Button>
      </div>
    )
  }

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
            <SelectItem value="week">Last 7 Days</SelectItem>
            {/* Future implementation: Add Month/Year logic */}
            {/* <SelectItem value="month">This Month</SelectItem> */}
            {/* <SelectItem value="year">This Year</SelectItem> */}
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
              {data.map((day) => {
                // Hardcoded goal or fetch from DailyLimits if possible. Defaulting to 2000 for vis.
                const percentage = Math.min((day.totalCalories / 2500) * 100, 100)
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
                      {day.totalCalories}
                    </span>
                  </div>
                )
              })}
            </div>
            <div className="mt-4 flex items-center justify-between text-xs text-muted-foreground">
              <span>0 kcal</span>
              {/* <span>Goal: 2000 kcal</span> */}
              <span>2500 kcal</span>
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
                  points={data
                    .map((d, i) => {
                      const x = (i / (data.length - 1)) * 280 + 10
                      // Normalize weight for the chart (y-axis range e.g. min-5 to max+5)
                      const minW = Math.min(...data.map(d => d.weight)) - 2
                      const maxW = Math.max(...data.map(d => d.weight)) + 2
                      const range = maxW - minW || 10 // Avoid division by zero

                      const y = 150 - ((d.weight - minW) / range) * 150
                      return `${x},${y}`
                    })
                    .join(" ")}
                />
                {/* Data points */}
                {data.map((d, i) => {
                  const x = (i / (data.length - 1)) * 280 + 10
                  const minW = Math.min(...data.map(d => d.weight)) - 2
                  const maxW = Math.max(...data.map(d => d.weight)) + 2
                  const range = maxW - minW || 10

                  const y = 150 - ((d.weight - minW) / range) * 150
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
              {data.map((d) => (
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
                {data.map((day, index) => {
                  // Todo: Fetch actual limit from DailyLimits or UserInfo
                  const targetCalories = 2000
                  const onTarget = day.totalCalories >= targetCalories - 200 && day.totalCalories <= targetCalories + 200
                  const prevWeight = index > 0 ? data[index - 1].weight : day.weight
                  const weightDiff = day.weight - prevWeight

                  return (
                    <tr key={day.day} className="border-b border-border last:border-0">
                      <td className="py-3 font-medium text-foreground">{day.day}</td>
                      <td className="py-3 text-foreground">{day.totalCalories} kcal</td>
                      <td className="py-3 text-foreground">{day.totalProtein}g</td>
                      <td className="py-3 text-foreground">
                        {day.weight}kg
                        {Math.abs(weightDiff) > 0.05 && (
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
                          {onTarget ? "On Target" : day.totalCalories < targetCalories - 200 ? "Under" : "Over"}
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
