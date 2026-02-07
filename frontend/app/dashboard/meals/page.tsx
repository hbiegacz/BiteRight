"use client"

import { useState, useEffect } from "react"
import { format } from "date-fns"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { Calendar } from "@/components/ui/calendar"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import {
  getMealsByDate,
  createMeal,
  deleteMeal,
  getAllUserMeals,
  searchIngredients,
  type Meal,
  type Ingredient,
} from "@/lib/api"
import {
  Plus,
  Search,
  CalendarIcon,
  Trash2,
  Coffee,
  Sun,
  Moon,
  Apple,
  Utensils,
  X,
  BookOpen,
  History,
  ArrowRight,
  Loader2,
  Info,
} from "lucide-react"
import Link from "next/link"
import { cn } from "@/lib/utils"
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
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert"

const mealTypes = [
  { id: 1, name: "Breakfast", icon: Coffee },
  { id: 2, name: "Lunch", icon: Sun },
  { id: 3, name: "Dinner", icon: Moon },
  { id: 4, name: "Snack", icon: Apple },
]

interface SelectedIngredient {
  ingredient: Ingredient
  amount: number
  unit: "g" | "portion"
}

export default function MealsPage() {
  const [selectedDate, setSelectedDate] = useState<Date>(new Date())
  const [meals, setMeals] = useState<Meal[]>([])
  const [recentMeals, setRecentMeals] = useState<Meal[]>([])
  const [loading, setLoading] = useState(true)
  const [dialogOpen, setDialogOpen] = useState(false)
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [mealToDelete, setMealToDelete] = useState<number | null>(null)

  // New meal form state
  const [mealName, setMealName] = useState("")
  const [mealDescription, setMealDescription] = useState("")
  const [mealTypeId, setMealTypeId] = useState<number>(1)
  const [searchQuery, setSearchQuery] = useState("")
  const [searchResults, setSearchResults] = useState<Ingredient[]>([])
  const [selectedIngredients, setSelectedIngredients] = useState<SelectedIngredient[]>([])
  const [searching, setSearching] = useState(false)
  const [creating, setCreating] = useState(false)

  const dateString = format(selectedDate, "yyyy-MM-dd")
  const isToday = dateString === format(new Date(), "yyyy-MM-dd")

  useEffect(() => {
    async function loadMeals() {
      setLoading(true)
      try {
        const [dayMeals, allMeals] = await Promise.all([
          getMealsByDate(dateString),
          getAllUserMeals(),
        ])
        setMeals(dayMeals)

        const sortedHistory = [...allMeals]
          .sort((a, b) => b.id - a.id)
          .slice(0, 3)

        setRecentMeals(sortedHistory)
      } catch (error) {
        console.error("Failed to load meals", error)
        try {
          const dayMeals = await getMealsByDate(dateString)
          setMeals(dayMeals)
        } catch (e) { console.error(e) }
      } finally {
        setLoading(false)
      }
    }
    loadMeals()
  }, [dateString])

  const handleSearch = async () => {
    if (!searchQuery.trim()) return
    setSearching(true)
    const results = await searchIngredients(searchQuery)
    setSearchResults(results)
    setSearching(false)
  }

  const addIngredient = (ingredient: Ingredient) => {
    if (!selectedIngredients.find((si) => si.ingredient.id === ingredient.id)) {
      setSelectedIngredients((prev) => [
        ...prev,
        { ingredient, amount: ingredient.portionSize, unit: "g" },
      ])
    }
    setSearchQuery("")
    setSearchResults([])
  }

  const removeIngredient = (id: number) => {
    setSelectedIngredients((prev) => prev.filter((si) => si.ingredient.id !== id))
  }

  const updateAmount = (id: number, amount: number) => {
    setSelectedIngredients((prev) =>
      prev.map((si) => (si.ingredient.id === id ? { ...si, amount } : si))
    )
  }

  const updateUnit = (id: number, unit: "g" | "portion") => {
    setSelectedIngredients((prev) =>
      prev.map((si) => (si.ingredient.id === id ? { ...si, unit } : si))
    )
  }

  const calculateMealNutrition = () => {
    let calories = 0
    let protein = 0
    let carbs = 0
    let fat = 0

    for (const { ingredient, amount, unit } of selectedIngredients) {
      const multiplier =
        unit === "portion" ? amount : amount / ingredient.portionSize
      calories += ingredient.calories * multiplier
      protein += ingredient.protein * multiplier
      carbs += ingredient.carbs * multiplier
      fat += ingredient.fat * multiplier
    }

    return { calories, protein, carbs, fat }
  }

  const handleCreateMeal = async () => {
    if (!mealName.trim() || selectedIngredients.length === 0) return

    setCreating(true)
    const result = await createMeal({
      mealTypeId,
      name: mealName,
      description: mealDescription,
      mealDate: dateString,
      contents: selectedIngredients.map((si) => ({
        ingredientId: si.ingredient.id,
        ingredientAmount:
          si.unit === "portion" ? si.amount * si.ingredient.portionSize : si.amount,
      })),
    })

    if (result) {
      setMeals((prev) => [...prev, result])
      setDialogOpen(false)
      resetForm()
    }
    setCreating(false)
  }

  const handleDeleteMeal = async () => {
    if (mealToDelete === null) return
    const success = await deleteMeal(mealToDelete)
    if (success) {
      setMeals((prev) => prev.filter((m) => m.id !== mealToDelete))
    }
    setDeleteDialogOpen(false)
    setMealToDelete(null)
  }

  const confirmDeleteMeal = (id: number) => {
    setMealToDelete(id)
    setDeleteDialogOpen(true)
  }

  const handleAddMealToType = (typeId: number) => {
    setMealTypeId(typeId)
    setDialogOpen(true)
  }

  const resetForm = () => {
    setMealName("")
    setMealDescription("")
    setMealTypeId(1)
    setSearchQuery("")
    setSearchResults([])
    setSelectedIngredients([])
  }

  const nutrition = calculateMealNutrition()

  return (
    <div className="mx-auto max-w-4xl space-y-6 pb-12">
      {/* Top Warning Alert */}
      <Alert className="border-primary/20 bg-primary/5">
        <Info className="h-4 w-4 text-primary" />
        <AlertTitle className="text-primary">Important Note</AlertTitle>
        <AlertDescription className="text-primary/80">
          Deleting a meal here will permanently remove it from the database and your nutrition history.
        </AlertDescription>
      </Alert>

      {/* Header */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-foreground sm:text-3xl">Calorie Tracking</h1>
          <p className="mt-1 text-muted-foreground">Log and manage your daily meals</p>
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
                Add Meal
              </Button>
            </DialogTrigger>
            <DialogContent className="max-h-[90vh] w-full overflow-y-auto sm:max-w-4xl">
              <DialogHeader>
                <DialogTitle>Log a Meal</DialogTitle>
              </DialogHeader>

              <div className="mt-4 grid gap-6 md:grid-cols-2">
                <div className="space-y-4">
                  <div className="grid gap-4 sm:grid-cols-2">
                    <div className="space-y-2">
                      <Label htmlFor="mealName">Meal Name</Label>
                      <Input
                        id="mealName"
                        placeholder="e.g., Morning Oatmeal"
                        value={mealName}
                        onChange={(e) => setMealName(e.target.value)}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="mealType">Meal Type</Label>
                      <Select
                        value={mealTypeId.toString()}
                        onValueChange={(v) => setMealTypeId(Number.parseInt(v))}
                      >
                        <SelectTrigger>
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                          {mealTypes.map((type) => (
                            <SelectItem key={type.id} value={type.id.toString()}>
                              {type.name}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="description">Description (optional)</Label>
                    <Input
                      id="description"
                      placeholder="Add a note..."
                      value={mealDescription}
                      onChange={(e) => setMealDescription(e.target.value)}
                    />
                  </div>

                  {/* Ingredient Search */}
                  <div className="space-y-2">
                    <Label>Add Ingredients</Label>
                    <div className="flex gap-2">
                      <Input
                        placeholder="Search ingredients..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        onKeyDown={(e) => e.key === "Enter" && handleSearch()}
                      />
                      <Button variant="outline" size="icon" onClick={handleSearch} disabled={searching}>
                        <Search className="h-4 w-4" />
                      </Button>
                    </div>

                    {/* Search Results */}
                    {(searchResults.length > 0 || searching || (searchQuery.trim() !== "" && !searching && searchResults.length === 0)) && (
                      <div className="max-h-40 overflow-y-auto rounded-lg border border-border">
                        {searching ? (
                          <div className="flex items-center justify-center p-4">
                            <Loader2 className="h-5 w-5 animate-spin text-muted-foreground" />
                          </div>
                        ) : searchResults.length > 0 ? (
                          searchResults.map((ingredient) => (
                            <button
                              key={ingredient.id}
                              type="button"
                              onClick={() => addIngredient(ingredient)}
                              className="flex w-full items-center justify-between p-3 text-left hover:bg-muted"
                            >
                              <div>
                                <p className="font-medium text-foreground">{ingredient.name}</p>
                                <p className="text-xs text-muted-foreground">
                                  {ingredient.brand} - {ingredient.portionSize}g
                                </p>
                              </div>
                              <span className="text-sm text-muted-foreground">
                                {ingredient.calories} kcal
                              </span>
                            </button>
                          ))
                        ) : (
                          <div className="p-4 text-center">
                            <p className="text-sm text-muted-foreground">No ingredients found.</p>
                          </div>
                        )}
                      </div>
                    )}
                  </div>

                  {/* Save Button for Mobile */}
                  <Button
                    className="w-full md:hidden"
                    onClick={handleCreateMeal}
                    disabled={!mealName.trim() || selectedIngredients.length === 0 || creating}
                  >
                    {creating ? "Saving..." : "Save Meal"}
                  </Button>
                </div>

                <div className="flex flex-col space-y-4">
                  {/* Selected Ingredients */}
                  <div className="flex-1 space-y-2">
                    <Label>Selected Ingredients</Label>
                    {selectedIngredients.length > 0 ? (
                      <div className="space-y-2">
                        {selectedIngredients.map(({ ingredient, amount, unit }) => (
                          <div
                            key={ingredient.id}
                            className="flex items-center gap-3 rounded-lg border border-border bg-muted/50 p-3"
                          >
                            <div className="flex-1">
                              <p className="font-medium text-foreground">{ingredient.name}</p>
                              <p className="text-xs text-muted-foreground">
                                {Math.round((ingredient.calories * amount) / ingredient.portionSize)} kcal
                              </p>
                            </div>
                            <div className="flex items-center gap-2">
                              <Input
                                type="number"
                                value={amount}
                                onChange={(e) => updateAmount(ingredient.id, Number(e.target.value))}
                                className="h-8 w-20"
                                min={0.1}
                                step={unit === "portion" ? 0.1 : 1}
                              />
                              <Select
                                value={unit}
                                onValueChange={(v) => updateUnit(ingredient.id, v as "g" | "portion")}
                              >
                                <SelectTrigger className="h-8 w-[100px]">
                                  <SelectValue />
                                </SelectTrigger>
                                <SelectContent>
                                  <SelectItem value="g">g</SelectItem>
                                  <SelectItem value="portion">portion(s)</SelectItem>
                                </SelectContent>
                              </Select>
                              <Button
                                variant="ghost"
                                size="icon"
                                className="h-8 w-8"
                                onClick={() => removeIngredient(ingredient.id)}
                              >
                                <X className="h-4 w-4" />
                              </Button>
                            </div>
                          </div>
                        ))}

                        {/* Meal Totals */}
                        <div className="rounded-lg bg-secondary/20 p-3">
                          <p className="text-sm font-medium text-foreground">Meal Totals</p>
                          <div className="mt-2 grid grid-cols-4 gap-2 text-center text-xs">
                            <div>
                              <p className="font-bold text-foreground">{Math.round(nutrition.calories)}</p>
                              <p className="text-muted-foreground">kcal</p>
                            </div>
                            <div>
                              <p className="font-bold text-secondary">{Math.round(nutrition.protein)}g</p>
                              <p className="text-muted-foreground">protein</p>
                            </div>
                            <div>
                              <p className="font-bold text-accent">{Math.round(nutrition.carbs)}g</p>
                              <p className="text-muted-foreground">carbs</p>
                            </div>
                            <div>
                              <p className="font-bold text-primary">{Math.round(nutrition.fat)}g</p>
                              <p className="text-muted-foreground">fat</p>
                            </div>
                          </div>
                        </div>
                      </div>
                    ) : (
                      <div className="flex h-40 flex-col items-center justify-center rounded-lg border border-dashed border-muted-foreground/25 p-4 text-center">
                        <Utensils className="mb-2 h-8 w-8 text-muted-foreground/50" />
                        <p className="text-sm text-muted-foreground">
                          Add ingredients from the search to build your meal
                        </p>
                      </div>
                    )}
                  </div>

                  {/* Save Button for Desktop */}
                  <Button
                    className="hidden w-full md:flex"
                    onClick={handleCreateMeal}
                    disabled={!mealName.trim() || selectedIngredients.length === 0 || creating}
                  >
                    {creating ? "Saving..." : "Save Meal"}
                  </Button>
                </div>
              </div>
            </DialogContent>
          </Dialog>
        </div>
      </div>

      {/* Meals List */}
      <div className="space-y-4">
        {loading ? (
          <div className="flex items-center justify-center py-12">
            <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
          </div>
        ) : (
          <div className="space-y-6">
            <div className="space-y-4">
              {mealTypes.map((type) => {
                const typeMeals = meals.filter(
                  (m) =>
                    m.mealTypeId === type.id || m.mealTypeName === type.name
                )
                const Icon = type.icon

                return (
                  <div key={type.id} className="rounded-xl border border-border bg-card overflow-hidden">
                    <div className="flex items-center justify-between border-b border-border bg-muted/30 px-4 py-3">
                      <div className="flex items-center gap-3">
                        <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-secondary/20">
                          <Icon className="h-4 w-4 text-secondary" />
                        </div>
                        <h3 className="font-semibold text-foreground">{type.name}</h3>
                      </div>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleAddMealToType(type.id)}
                        className="h-8 gap-1.5 text-xs font-medium"
                      >
                        <Plus className="h-3.5 w-3.5" />
                        Add {type.name}
                      </Button>
                    </div>
                    <div className="divide-y divide-border">
                      {typeMeals.length > 0 ? (
                        typeMeals.map((meal) => (
                          <div key={meal.id} className="flex items-center justify-between p-4">
                            <div>
                              <p className="font-medium text-foreground">{meal.name}</p>
                              <p className="text-sm text-muted-foreground">
                                {meal.contents.length} item{meal.contents.length !== 1 ? "s" : ""}
                                {meal.description && ` • ${meal.description}`}
                              </p>
                            </div>
                            <div className="flex items-center gap-3">
                              <Button
                                variant="ghost"
                                size="icon"
                                className="text-destructive hover:bg-destructive/10 hover:text-destructive"
                                onClick={() => confirmDeleteMeal(meal.id)}
                              >
                                <Trash2 className="h-4 w-4" />
                              </Button>
                            </div>
                          </div>
                        ))
                      ) : (
                        <div className="flex flex-col items-center justify-center py-8 text-center">
                          <p className="text-sm text-muted-foreground">Waiting for you to add a meal here</p>
                          <Button
                            variant="link"
                            size="sm"
                            onClick={() => handleAddMealToType(type.id)}
                            className="mt-1 h-auto p-0 text-xs"
                          >
                            Add your first {type.name.toLowerCase()}
                          </Button>
                        </div>
                      )}
                    </div>
                  </div>
                )
              })}
              </div>

              {/* Always show History and Inspiration at the bottom */}
              <div className="grid gap-6 md:grid-cols-2 pt-4">
                {/* Recent Meals Section */}
                <div className="rounded-xl border border-border bg-card p-6">
                  <div className="mb-4 flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <History className="h-5 w-5 text-primary" />
                      <h3 className="font-semibold text-foreground">Recent Meals</h3>
                    </div>
                  </div>
                  {recentMeals.length > 0 ? (
                    <div className="space-y-3">
                      {recentMeals.map((meal) => (
                        <div key={meal.id} className="flex items-center justify-between rounded-lg bg-muted/30 p-3">
                          <div>
                            <p className="font-medium text-foreground max-w-[150px] truncate sm:max-w-[200px]">
                              {meal.name}
                            </p>
                            <p className="text-xs text-muted-foreground">{meal.mealTypeName}</p>
                          </div>
                          <div className="text-xs text-muted-foreground">
                            {format(new Date(meal.mealDate), "MMM d")}
                          </div>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <p className="text-sm text-muted-foreground">No meal history yet.</p>
                  )}
                </div>

                {/* Recipe Inspiration Section */}
                <div className="flex flex-col justify-between rounded-xl border border-border bg-gradient-to-br from-primary/10 to-transparent p-6">
                  <div>
                    <div className="mb-2 flex h-10 w-10 items-center justify-center rounded-full bg-primary/20">
                      <BookOpen className="h-5 w-5 text-primary" />
                    </div>
                    <h3 className="mb-2 font-semibold text-foreground">Need Inspiration?</h3>
                    <p className="text-sm text-muted-foreground">
                      Discover healthy and delicious recipes to add to your daily plan.
                    </p>
                  </div>
                  <Button asChild variant="outline" className="mt-6 w-full justify-between bg-background">
                    <Link href="/dashboard/recipes">
                      Browse Recipes
                      <ArrowRight className="h-4 w-4" />
                    </Link>
                  </Button>
                </div>
              </div>
          </div>
        )}
      </div>

      {/* Delete Confirmation Dialog */}
      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Are you absolutely sure?</AlertDialogTitle>
            <AlertDialogDescription>
              This action cannot be undone. This will permanently delete your meal
              and remove the data from our servers.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel onClick={() => setMealToDelete(null)}>Cancel</AlertDialogCancel>
            <AlertDialogAction
              className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
              onClick={handleDeleteMeal}
            >
              Delete
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}
