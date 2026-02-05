"use client"

import { useState, useEffect, useCallback } from "react"
import { format } from "date-fns"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
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
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog"
import {
  Plus,
  CalendarIcon,
  Dumbbell,
  Flame,
  Clock,
  Search,
  Loader2,
  Trash2,
  Edit2,
  X,
} from "lucide-react"
import { cn } from "@/lib/utils"
import {
  type UserExercise,
  type ExerciseInfo,
  getExercisesByDate,
  searchExerciseInfo,
  createUserExercise,
  updateUserExercise,
  deleteUserExercise,
} from "@/lib/api"

export default function ExercisesPage() {
  const [selectedDate, setSelectedDate] = useState<Date>(new Date())
  const [exercises, setExercises] = useState<UserExercise[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editingExercise, setEditingExercise] = useState<UserExercise | null>(null)
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [exerciseToDelete, setExerciseToDelete] = useState<UserExercise | null>(null)

  // Form state
  const [searchQuery, setSearchQuery] = useState("")
  const [searchResults, setSearchResults] = useState<ExerciseInfo[]>([])
  const [isSearching, setIsSearching] = useState(false)
  const [selectedExercise, setSelectedExercise] = useState<ExerciseInfo | null>(null)
  const [duration, setDuration] = useState(30)
  const [isSubmitting, setIsSubmitting] = useState(false)

  const dateString = format(selectedDate, "yyyy-MM-dd")
  const isToday = dateString === format(new Date(), "yyyy-MM-dd")

  const totalCaloriesBurned = exercises.reduce((sum, e) => sum + e.caloriesBurnt, 0)
  const totalDuration = exercises.reduce((sum, e) => sum + e.duration, 0)

  // Fetch exercises for selected date
  const fetchExercises = useCallback(async () => {
    setIsLoading(true)
    const data = await getExercisesByDate(dateString)
    setExercises(data?.content || [])
    setIsLoading(false)
  }, [dateString])

  useEffect(() => {
    fetchExercises()
  }, [fetchExercises])

  // Search for exercise types
  useEffect(() => {
    const searchExercises = async () => {
      if (searchQuery.length < 2) {
        setSearchResults([])
        return
      }
      setIsSearching(true)
      const results = await searchExerciseInfo(searchQuery)
      setSearchResults(results)
      setIsSearching(false)
    }

    const debounce = setTimeout(searchExercises, 300)
    return () => clearTimeout(debounce)
  }, [searchQuery])

  const resetForm = () => {
    setSearchQuery("")
    setSearchResults([])
    setSelectedExercise(null)
    setDuration(30)
    setEditingExercise(null)
  }

  const handleOpenDialog = (exercise?: UserExercise) => {
    if (exercise) {
      setEditingExercise(exercise)
      setSelectedExercise({
        id: exercise.exerciseInfoId,
        name: exercise.activityName,
        caloriesPerMinute: exercise.caloriesBurnt / exercise.duration,
      })
      setDuration(exercise.duration)
    } else {
      resetForm()
    }
    setDialogOpen(true)
  }

  const handleCloseDialog = () => {
    setDialogOpen(false)
    resetForm()
  }

  const handleSubmit = async () => {
    if (!selectedExercise?.id) return

    setIsSubmitting(true)

    const exerciseData = {
      exerciseInfoId: selectedExercise.id,
      activityDate: dateString,
      duration,
    }

    let success: boolean
    if (editingExercise) {
      const result = await updateUserExercise(editingExercise.id, exerciseData)
      success = !!result
    } else {
      const result = await createUserExercise(exerciseData)
      success = !!result
    }

    if (success) {
      await fetchExercises()
      handleCloseDialog()
    }

    setIsSubmitting(false)
  }

  const handleDeleteClick = (exercise: UserExercise) => {
    setExerciseToDelete(exercise)
    setDeleteDialogOpen(true)
  }

  const handleConfirmDelete = async () => {
    if (!exerciseToDelete) return

    const success = await deleteUserExercise(exerciseToDelete.id)
    if (success) {
      await fetchExercises()
    }

    setDeleteDialogOpen(false)
    setExerciseToDelete(null)
  }

  const estimatedCalories = selectedExercise
    ? Math.round(duration * selectedExercise.caloriesPerMinute)
    : 0

  return (
    <div className="mx-auto max-w-4xl space-y-6">
      {/* Header */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-foreground sm:text-3xl">Exercise Tracking</h1>
          <p className="mt-1 text-muted-foreground">Log your workouts and track calories burned</p>
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

          <Dialog open={dialogOpen} onOpenChange={(open) => !open && handleCloseDialog()}>
            <DialogTrigger asChild>
              <Button className="gap-2" onClick={() => handleOpenDialog()}>
                <Plus className="h-4 w-4" />
                Log Exercise
              </Button>
            </DialogTrigger>
            <DialogContent className="max-w-md">
              <DialogHeader>
                <DialogTitle>
                  {editingExercise ? "Edit Exercise" : "Log Exercise"}
                </DialogTitle>
              </DialogHeader>

              <div className="mt-4 space-y-4">
                {/* Exercise Search */}
                <div className="space-y-2">
                  <Label>Search Exercise Type</Label>
                  <div className="relative">
                    <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                    <Input
                      placeholder="Search exercises (e.g., Running, Cycling)..."
                      value={searchQuery}
                      onChange={(e) => setSearchQuery(e.target.value)}
                      className="pl-10"
                    />
                  </div>

                  {/* Search Results */}
                  {(searchResults.length > 0 || isSearching) && !selectedExercise && (
                    <div className="rounded-lg border border-border bg-card max-h-48 overflow-y-auto">
                      {isSearching ? (
                        <div className="flex items-center justify-center p-4">
                          <Loader2 className="h-5 w-5 animate-spin text-muted-foreground" />
                        </div>
                      ) : (
                        searchResults.map((exercise) => (
                          <button
                            key={exercise.id}
                            type="button"
                            className="w-full flex items-center justify-between p-3 hover:bg-muted/50 transition-colors text-left border-b border-border last:border-0"
                            onClick={() => {
                              setSelectedExercise(exercise)
                              setSearchQuery("")
                              setSearchResults([])
                            }}
                          >
                            <div className="flex items-center gap-3">
                              <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10">
                                <Dumbbell className="h-5 w-5 text-primary" />
                              </div>
                              <span className="font-medium text-foreground">{exercise.name}</span>
                            </div>
                            <span className="text-sm text-muted-foreground">
                              {exercise.caloriesPerMinute} kcal/min
                            </span>
                          </button>
                        ))
                      )}
                    </div>
                  )}
                </div>

                {/* Selected Exercise */}
                {selectedExercise && (
                  <div className="rounded-lg border border-primary/30 bg-primary/5 p-3">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/20">
                          <Dumbbell className="h-5 w-5 text-primary" />
                        </div>
                        <div>
                          <p className="font-medium text-foreground">{selectedExercise.name}</p>
                          <p className="text-sm text-muted-foreground">
                            {selectedExercise.caloriesPerMinute} kcal/min
                          </p>
                        </div>
                      </div>
                      <Button
                        variant="ghost"
                        size="icon"
                        className="h-8 w-8"
                        onClick={() => setSelectedExercise(null)}
                      >
                        <X className="h-4 w-4" />
                      </Button>
                    </div>
                  </div>
                )}

                {/* Duration */}
                <div className="space-y-2">
                  <Label htmlFor="duration">Duration (minutes)</Label>
                  <Input
                    id="duration"
                    type="number"
                    value={duration}
                    onChange={(e) => setDuration(Number(e.target.value))}
                    min={1}
                    max={480}
                  />
                </div>

                {/* Estimated Calories */}
                {selectedExercise && (
                  <div className="rounded-lg bg-muted p-4">
                    <div className="flex items-center gap-3">
                      <div className="flex h-12 w-12 items-center justify-center rounded-full bg-accent/20">
                        <Flame className="h-6 w-6 text-accent" />
                      </div>
                      <div>
                        <p className="text-sm text-muted-foreground">Estimated calories burned</p>
                        <p className="text-2xl font-bold text-foreground">{estimatedCalories} kcal</p>
                      </div>
                    </div>
                  </div>
                )}

                <Button
                  className="w-full"
                  onClick={handleSubmit}
                  disabled={!selectedExercise || duration < 1 || isSubmitting}
                >
                  {isSubmitting ? (
                    <>
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                      {editingExercise ? "Updating..." : "Adding..."}
                    </>
                  ) : editingExercise ? (
                    "Update Exercise"
                  ) : (
                    "Add Exercise"
                  )}
                </Button>
              </div>
            </DialogContent>
          </Dialog>
        </div>
      </div>

      {/* Summary Cards */}
      <div className="grid gap-4 sm:grid-cols-2">
        <div className="rounded-xl border border-border bg-card p-5">
          <div className="flex items-center gap-3">
            <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-accent/20">
              <Flame className="h-6 w-6 text-accent" />
            </div>
            <div>
              <p className="text-sm text-muted-foreground">Calories Burned</p>
              <p className="text-2xl font-bold text-foreground">{totalCaloriesBurned} kcal</p>
            </div>
          </div>
        </div>

        <div className="rounded-xl border border-border bg-card p-5">
          <div className="flex items-center gap-3">
            <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-secondary/20">
              <Clock className="h-6 w-6 text-secondary" />
            </div>
            <div>
              <p className="text-sm text-muted-foreground">Total Duration</p>
              <p className="text-2xl font-bold text-foreground">{totalDuration} min</p>
            </div>
          </div>
        </div>
      </div>

      {/* Exercise List */}
      <div className="space-y-4">
        <h2 className="text-lg font-semibold text-foreground">
          {isToday ? "Today's Workouts" : `Workouts for ${format(selectedDate, "MMM d")}`}
        </h2>

        {isLoading ? (
          <div className="rounded-2xl border border-border bg-card p-12">
            <div className="flex flex-col items-center justify-center">
              <Loader2 className="h-8 w-8 animate-spin text-primary" />
              <p className="mt-3 text-muted-foreground">Loading exercises...</p>
            </div>
          </div>
        ) : exercises.length === 0 ? (
          <div className="rounded-2xl border border-border bg-card p-12 text-center">
            <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-muted">
              <Dumbbell className="h-8 w-8 text-muted-foreground" />
            </div>
            <h3 className="mt-4 text-lg font-semibold text-foreground">No workouts logged</h3>
            <p className="mt-1 text-muted-foreground">
              Start tracking your fitness by logging an exercise
            </p>
            <Button className="mt-6" onClick={() => handleOpenDialog()}>
              <Plus className="mr-2 h-4 w-4" />
              Log Your First Workout
            </Button>
          </div>
        ) : (
          <div className="space-y-3">
            {exercises.map((exercise) => (
              <div
                key={exercise.id}
                className="group flex items-center gap-4 rounded-xl border border-border bg-card p-4 transition-shadow hover:shadow-md"
              >
                <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-lg bg-primary/20">
                  <Dumbbell className="h-6 w-6 text-primary" />
                </div>
                <div className="flex-1 min-w-0">
                  <h4 className="font-medium text-foreground truncate">{exercise.activityName}</h4>
                  <p className="text-sm text-muted-foreground">
                    {exercise.duration} minutes
                  </p>
                </div>
                <div className="text-right">
                  <p className="font-semibold text-accent">{exercise.caloriesBurnt} kcal</p>
                  <p className="text-xs text-muted-foreground">burned</p>
                </div>
                <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                  <Button
                    variant="ghost"
                    size="icon"
                    className="h-8 w-8"
                    onClick={() => handleOpenDialog(exercise)}
                  >
                    <Edit2 className="h-4 w-4" />
                  </Button>
                  <Button
                    variant="ghost"
                    size="icon"
                    className="h-8 w-8 text-destructive hover:text-destructive"
                    onClick={() => handleDeleteClick(exercise)}
                  >
                    <Trash2 className="h-4 w-4" />
                  </Button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Delete Confirmation Dialog */}
      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Delete Exercise</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to delete "{exerciseToDelete?.activityName}"? This action cannot
              be undone.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleConfirmDelete}
              className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
            >
              Delete
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}
