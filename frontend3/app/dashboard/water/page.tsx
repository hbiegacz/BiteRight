"use client"

import { useState, useEffect } from "react"
import { format } from "date-fns"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Calendar } from "@/components/ui/calendar"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import {
  getWaterIntakeByDate,
  addWaterIntake,
  getDailyLimits,
  type WaterIntake,
} from "@/lib/api"
import { Plus, CalendarIcon, Droplets, GlassWater } from "lucide-react"
import { cn } from "@/lib/utils"

const quickAmounts = [250, 500, 750, 1000]

export default function WaterPage() {
  const [selectedDate, setSelectedDate] = useState<Date>(new Date())
  const [waterIntakes, setWaterIntakes] = useState<WaterIntake[]>([])
  const [waterGoal, setWaterGoal] = useState(2500)
  const [dialogOpen, setDialogOpen] = useState(false)
  const [customAmount, setCustomAmount] = useState(250)
  const [loading, setLoading] = useState(true)

  const dateString = format(selectedDate, "yyyy-MM-dd")
  const isToday = dateString === format(new Date(), "yyyy-MM-dd")

  const totalWater = waterIntakes.reduce((sum, w) => sum + w.waterAmount, 0)
  const percentage = Math.min((totalWater / waterGoal) * 100, 100)
  const glasses = Math.floor(totalWater / 250)

  useEffect(() => {
    async function loadData() {
      setLoading(true)
      const [intakes, limits] = await Promise.all([
        getWaterIntakeByDate(dateString),
        getDailyLimits(),
      ])
      setWaterIntakes(intakes)
      if (limits) {
        setWaterGoal(limits.waterGoal)
      }
      setLoading(false)
    }
    loadData()
  }, [dateString])

  const handleAddWater = async (amount: number) => {
    const result = await addWaterIntake(amount, dateString)
    if (result) {
      setWaterIntakes((prev) => [...prev, result])
    }
    setDialogOpen(false)
  }

  return (
    <div className="mx-auto max-w-4xl space-y-6">
      {/* Header */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-foreground sm:text-3xl">Water Intake</h1>
          <p className="mt-1 text-muted-foreground">Stay hydrated throughout the day</p>
        </div>

        <div className="flex items-center gap-3">
          <Popover>
            <PopoverTrigger asChild>
              <Button variant="outline" className="min-w-[180px] justify-start bg-transparent">
                <CalendarIcon className="mr-2 h-4 w-4" />
                {isToday ? "Today" : format(selectedDate, "MMM d, yyyy")}
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

          <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
            <DialogTrigger asChild>
              <Button className="gap-2">
                <Plus className="h-4 w-4" />
                Add Water
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Add Water Intake</DialogTitle>
              </DialogHeader>

              <div className="mt-4 space-y-4">
                <div className="grid grid-cols-2 gap-3">
                  {quickAmounts.map((amount) => (
                    <Button
                      key={amount}
                      variant="outline"
                      className="h-16 flex-col gap-1 bg-transparent"
                      onClick={() => handleAddWater(amount)}
                    >
                      <GlassWater className="h-5 w-5 text-blue-500" />
                      <span>{amount}ml</span>
                    </Button>
                  ))}
                </div>

                <div className="relative">
                  <div className="absolute inset-0 flex items-center">
                    <span className="w-full border-t border-border" />
                  </div>
                  <div className="relative flex justify-center text-xs uppercase">
                    <span className="bg-background px-2 text-muted-foreground">
                      Or custom amount
                    </span>
                  </div>
                </div>

                <div className="flex gap-2">
                  <Input
                    type="number"
                    value={customAmount}
                    onChange={(e) => setCustomAmount(Number(e.target.value))}
                    min={1}
                    placeholder="Amount in ml"
                  />
                  <Button onClick={() => handleAddWater(customAmount)}>Add</Button>
                </div>
              </div>
            </DialogContent>
          </Dialog>
        </div>
      </div>

      {/* Main Water Display */}
      <div className="rounded-2xl border border-border bg-card p-8">
        <div className="flex flex-col items-center">
          {/* Water Tank Visualization */}
          <div className="relative h-64 w-32">
            <div className="absolute inset-0 rounded-2xl border-4 border-blue-200 bg-blue-50/50 overflow-hidden">
              <div
                className="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-blue-500 to-blue-400 transition-all duration-700"
                style={{ height: `${percentage}%` }}
              >
                {/* Water wave effect */}
                <div className="absolute inset-x-0 -top-2 h-4">
                  <svg
                    viewBox="0 0 100 20"
                    preserveAspectRatio="none"
                    className="h-full w-full fill-blue-400"
                  >
                    <path d="M0 10 Q25 0 50 10 T100 10 V20 H0 Z" />
                  </svg>
                </div>
              </div>
            </div>
            {/* Measurement lines */}
            {[25, 50, 75].map((line) => (
              <div
                key={line}
                className="absolute left-0 right-0 flex items-center"
                style={{ bottom: `${line}%` }}
              >
                <div className="h-px flex-1 bg-blue-200" />
                <span className="ml-2 text-xs text-muted-foreground">{line}%</span>
              </div>
            ))}
          </div>

          {/* Stats */}
          <div className="mt-6 text-center">
            <p className="text-4xl font-bold text-foreground">
              {(totalWater / 1000).toFixed(1)}L
            </p>
            <p className="mt-1 text-muted-foreground">
              of {(waterGoal / 1000).toFixed(1)}L goal
            </p>
            <div className="mt-4 flex items-center justify-center gap-2">
              <Droplets className="h-5 w-5 text-blue-500" />
              <span className="text-lg font-medium text-foreground">
                {glasses} glasses today
              </span>
            </div>
          </div>

          {/* Quick Add Buttons */}
          <div className="mt-6 flex flex-wrap justify-center gap-2">
            {quickAmounts.slice(0, 3).map((amount) => (
              <Button
                key={amount}
                variant="outline"
                size="sm"
                onClick={() => handleAddWater(amount)}
                className="gap-1"
              >
                <Plus className="h-3 w-3" />
                {amount}ml
              </Button>
            ))}
          </div>
        </div>
      </div>

      {/* Intake History */}
      <div className="rounded-xl border border-border bg-card p-5">
        <h2 className="font-semibold text-foreground">
          {isToday ? "Today's Intake" : `Intake for ${format(selectedDate, "MMM d")}`}
        </h2>

        {loading ? (
          <div className="flex items-center justify-center py-8">
            <div className="h-6 w-6 animate-spin rounded-full border-2 border-primary border-t-transparent" />
          </div>
        ) : waterIntakes.length === 0 ? (
          <div className="py-8 text-center">
            <Droplets className="mx-auto h-10 w-10 text-muted-foreground/50" />
            <p className="mt-2 text-sm text-muted-foreground">No water logged yet</p>
          </div>
        ) : (
          <div className="mt-4 space-y-2">
            {waterIntakes.map((intake, index) => (
              <div
                key={intake.id || index}
                className="flex items-center justify-between rounded-lg bg-muted/50 p-3"
              >
                <div className="flex items-center gap-3">
                  <div className="flex h-8 w-8 items-center justify-center rounded-full bg-blue-500/20">
                    <GlassWater className="h-4 w-4 text-blue-500" />
                  </div>
                  <span className="font-medium text-foreground">{intake.waterAmount}ml</span>
                </div>
                <span className="text-sm text-muted-foreground">
                  {Math.round(intake.waterAmount / 250)} glass{intake.waterAmount > 250 ? "es" : ""}
                </span>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
