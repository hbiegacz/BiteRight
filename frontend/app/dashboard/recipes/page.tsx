"use client"

import { useState, useEffect, useCallback } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
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
import {
  Search,
  BookOpen,
  Plus,
  Loader2,
  X,
  Utensils,
  Flame,
  Dna,
  Wheat,
  Droplets,
} from "lucide-react"
import {
  searchRecipes,
  createRecipe,
  searchIngredients,
  type RecipeDTO,
  type Ingredient,
} from "@/lib/api"

interface SelectedIngredient {
  ingredient: Ingredient
  amount: number
  unit: "g" | "portion"
}

export default function RecipesPage() {
  const [searchQuery, setSearchQuery] = useState("")
  const [recipes, setRecipes] = useState<RecipeDTO[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [createDialogOpen, setCreateDialogOpen] = useState(false)
  const [selectedRecipe, setSelectedRecipe] = useState<RecipeDTO | null>(null)
  const [isViewOpen, setIsViewOpen] = useState(false)

  const [newName, setNewName] = useState("")
  const [newDescription, setNewDescription] = useState("")
  const [newImageUrl, setNewImageUrl] = useState("")
  const [ingredientSearch, setIngredientSearch] = useState("")
  const [ingredientResults, setIngredientResults] = useState<Ingredient[]>([])
  const [selectedIngredients, setSelectedIngredients] = useState<SelectedIngredient[]>([])
  const [isSearchingIngredients, setIsSearchingIngredients] = useState(false)
  const [isCreating, setIsCreating] = useState(false)

  const fetchRecipes = useCallback(async (query: string) => {
    setIsLoading(true)
    const data = await searchRecipes(query || " ")
    setRecipes(data)
    setIsLoading(false)
  }, [])

  useEffect(() => {
    const timer = setTimeout(() => {
      fetchRecipes(searchQuery)
    }, 300)
    return () => clearTimeout(timer)
  }, [searchQuery, fetchRecipes])

  const handleIngredientSearch = async () => {
    if (!ingredientSearch.trim()) return
    setIsSearchingIngredients(true)
    const results = await searchIngredients(ingredientSearch)
    setIngredientResults(results)
    setIsSearchingIngredients(false)
  }

  const addIngredient = (ingredient: Ingredient) => {
    if (!selectedIngredients.find(si => si.ingredient.id === ingredient.id)) {
      setSelectedIngredients(prev => [...prev, { ingredient, amount: ingredient.portionSize, unit: "g" }])
    }
    setIngredientSearch("")
    setIngredientResults([])
  }

  const removeIngredient = (id: number) => {
    setSelectedIngredients(prev => prev.filter(si => si.ingredient.id !== id))
  }

  const updateAmount = (id: number, amount: number) => {
    setSelectedIngredients(prev => prev.map(si => si.ingredient.id === id ? { ...si, amount } : si))
  }

  const updateUnit = (id: number, unit: "g" | "portion") => {
    setSelectedIngredients(prev => prev.map(si => si.ingredient.id === id ? { ...si, unit } : si))
  }

  const handleCreateRecipe = async () => {
    if (!newName.trim() || selectedIngredients.length === 0) return
    setIsCreating(true)
    const success = await createRecipe({
      name: newName,
      description: newDescription,
      imageUrl: newImageUrl,
      contents: selectedIngredients.map(si => ({
        ingredientId: si.ingredient.id,
        ingredientAmount: si.unit === "portion" ? si.amount * si.ingredient.portionSize : si.amount
      }))
    })
    if (success) {
      setCreateDialogOpen(false)
      resetForm()
      fetchRecipes(searchQuery)
    }
    setIsCreating(false)
  }

  const resetForm = () => {
    setNewName("")
    setNewDescription("")
    setNewImageUrl("")
    setSelectedIngredients([])
    setIngredientResults([])
    setIngredientSearch("")
  }

  const calculateTotals = (recipe: RecipeDTO) => {
    let calories = 0, protein = 0, carbs = 0, fat = 0
    recipe.contents?.forEach(c => {
      const multiplier = c.ingredientAmount / (c.ingredientPortionSize || 100)
      calories += (c.ingredientCalories || 0) * multiplier
      protein += (c.ingredientProtein || 0) * multiplier
      carbs += (c.ingredientCarbs || 0) * multiplier
      fat += (c.ingredientFat || 0) * multiplier
    })
    return { calories, protein, carbs, fat }
  }

  return (
    <div className="mx-auto max-w-6xl space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-foreground sm:text-3xl">Recipe Search</h1>
          <p className="mt-1 text-muted-foreground">Discover healthy recipes that fit your nutritional goals</p>
        </div>

        <Dialog open={createDialogOpen} onOpenChange={(open) => {
          setCreateDialogOpen(open)
          if (!open) resetForm()
        }}>
          <DialogTrigger asChild>
            <Button className="gap-2">
              <Plus className="h-4 w-4" />
              Create Recipe
            </Button>
          </DialogTrigger>
          <DialogContent className="max-h-[90vh] w-full overflow-y-auto sm:max-w-2xl">
            <DialogHeader>
              <DialogTitle>Create New Recipe</DialogTitle>
            </DialogHeader>
            <div className="mt-4 space-y-4">
              <div className="grid gap-4 sm:grid-cols-2">
                <div className="space-y-2">
                  <Label htmlFor="name">Recipe Name</Label>
                  <Input id="name" value={newName} onChange={e => setNewName(e.target.value)} placeholder="e.g. Scrambled Eggs" />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="imageUrl">Image URL (optional)</Label>
                  <Input id="imageUrl" value={newImageUrl} onChange={e => setNewImageUrl(e.target.value)} placeholder="https://example.com/image.jpg" />
                </div>
              </div>
              <div className="space-y-2">
                <Label htmlFor="desc">Description</Label>
                <Input id="desc" value={newDescription} onChange={e => setNewDescription(e.target.value)} placeholder="A quick and easy breakfast..." />
              </div>

              <div className="space-y-2">
                <Label>Ingredients</Label>
                <div className="flex gap-2">
                  <Input
                    placeholder="Search ingredients..."
                    value={ingredientSearch}
                    onChange={e => setIngredientSearch(e.target.value)}
                    onKeyDown={e => e.key === "Enter" && handleIngredientSearch()}
                  />
                  <Button variant="outline" size="icon" onClick={handleIngredientSearch} disabled={isSearchingIngredients}>
                    <Search className="h-4 w-4" />
                  </Button>
                </div>

                {ingredientResults.length > 0 && (
                  <div className="max-h-40 overflow-y-auto rounded-lg border border-border">
                    {ingredientResults.map((ing) => (
                      <button
                        key={ing.id}
                        type="button"
                        onClick={() => addIngredient(ing)}
                        className="flex w-full items-center justify-between p-3 text-left hover:bg-muted"
                      >
                        <div>
                          <p className="font-medium text-foreground">{ing.name}</p>
                          <p className="text-xs text-muted-foreground">{ing.brand} • {ing.portionSize}g</p>
                        </div>
                        <span className="text-sm text-muted-foreground">{ing.calories} kcal</span>
                      </button>
                    ))}
                  </div>
                )}

                <div className="mt-2 space-y-2">
                  {selectedIngredients.map((si) => (
                    <div key={si.ingredient.id} className="flex items-center gap-3 rounded-lg border border-border bg-muted/50 p-2 px-3">
                      <span className="flex-1 text-sm font-medium truncate">{si.ingredient.name}</span>
                      <div className="flex items-center gap-2">
                        <Input
                          type="number"
                          className="h-8 w-16 text-xs"
                          value={si.amount}
                          onChange={e => updateAmount(si.ingredient.id, Number(e.target.value))}
                          min={1}
                        />
                        <Select
                          value={si.unit}
                          onValueChange={v => updateUnit(si.ingredient.id, v as "g" | "portion")}
                        >
                          <SelectTrigger className="h-8 w-24 text-[10px]">
                            <SelectValue />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="g">Grams</SelectItem>
                            <SelectItem value="portion">Portion(s)</SelectItem>
                          </SelectContent>
                        </Select>
                        <Button variant="ghost" size="icon" className="h-7 w-7" onClick={() => removeIngredient(si.ingredient.id)}>
                          <X className="h-4 w-4" />
                        </Button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              <Button className="w-full" onClick={handleCreateRecipe} disabled={!newName.trim() || selectedIngredients.length === 0 || isCreating}>
                {isCreating ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> : "Save Recipe"}
              </Button>
            </div>
          </DialogContent>
        </Dialog>
      </div>

      <div className="relative">
        <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          placeholder="Search recipes..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="pl-10"
        />
      </div>

      {isLoading ? (
        <div className="flex justify-center py-20">
          <Loader2 className="h-10 w-10 animate-spin text-primary" />
        </div>
      ) : recipes.length === 0 ? (
        <div className="rounded-2xl border border-border bg-card p-12 text-center">
          <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-muted">
            <BookOpen className="h-8 w-8 text-muted-foreground" />
          </div>
          <h3 className="mt-4 text-lg font-semibold text-foreground">No recipes found</h3>
          <p className="mt-1 text-muted-foreground">Try adjusting your search</p>
        </div>
      ) : (
        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {recipes.map((recipe) => (
            <div key={recipe.recipeId} className="group overflow-hidden rounded-xl border border-border bg-card transition-all hover:shadow-lg">
              <div className="relative aspect-video bg-muted overflow-hidden">
                <img
                  src={recipe.imageUrl || "https://images.unsplash.com/photo-1495195129352-aed325a55b65?q=80&w=400&h=225&auto=format&fit=crop"}
                  alt={recipe.name}
                  className="h-full w-full object-cover transition-transform group-hover:scale-105"
                  onError={(e) => {
                    (e.target as HTMLImageElement).src = "https://images.unsplash.com/photo-1495195129352-aed325a55b65?q=80&w=400&h=225&auto=format&fit=crop"
                  }}
                />
              </div>
              <div className="p-4">
                <h3 className="font-semibold text-foreground group-hover:text-primary transition-colors">{recipe.name}</h3>
                <p className="mt-1 text-sm text-muted-foreground line-clamp-2 min-h-[40px]">{recipe.description}</p>
                <div className="mt-4 flex items-center justify-between">
                  <div className="flex items-center gap-1 text-xs text-muted-foreground">
                    <Utensils className="h-3 w-3" />
                    <span>{recipe.contents?.length || 0} ingredients</span>
                  </div>
                  <Button
                    variant="ghost"
                    size="sm"
                    className="h-8 text-primary hover:text-primary hover:bg-primary/10"
                    onClick={() => {
                      setSelectedRecipe(recipe)
                      setIsViewOpen(true)
                    }}
                  >
                    View Details
                  </Button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      <Dialog open={isViewOpen} onOpenChange={setIsViewOpen}>
        <DialogContent className="w-full max-w-md sm:max-w-2xl lg:max-w-4xl p-0 overflow-hidden sm:rounded-2xl border-none">
          {selectedRecipe && (
            <div className="grid md:grid-cols-2 min-h-[500px]">
              <div className="relative h-64 md:h-auto overflow-hidden">
                <img
                  src={selectedRecipe.imageUrl || "https://images.unsplash.com/photo-1495195129352-aed325a55b65?q=80&w=800&auto=format&fit=crop"}
                  alt={selectedRecipe.name}
                  className="absolute inset-0 h-full w-full object-cover"
                  onError={(e) => {
                    (e.target as HTMLImageElement).src = "https://images.unsplash.com/photo-1495195129352-aed325a55b65?q=80&w=800&auto=format&fit=crop"
                  }}
                />
                <div className="absolute inset-x-0 bottom-0 h-24 bg-gradient-to-t from-black/60 to-transparent md:hidden" />
              </div>
              <div className="flex flex-col p-6 md:p-10 space-y-8 overflow-y-auto max-h-[90vh]">
                <div>
                  <h2 className="text-4xl font-bold tracking-tight text-foreground">{selectedRecipe.name}</h2>
                  <div className="mt-4 h-1 w-12 bg-primary rounded-full" />
                  <p className="mt-6 text-muted-foreground leading-relaxed text-lg">{selectedRecipe.description}</p>
                </div>

                <div className="grid grid-cols-4 gap-4 p-5 rounded-2xl bg-muted/50 border border-border/50 backdrop-blur-sm">
                  {(() => {
                    const t = calculateTotals(selectedRecipe)
                    return (
                      <>
                        <div className="text-center space-y-1">
                          <Flame className="h-4 w-4 mx-auto text-orange-500" />
                          <p className="text-xl font-bold">{Math.round(t.calories)}</p>
                          <p className="text-[10px] uppercase font-bold text-muted-foreground">Kcal</p>
                        </div>
                        <div className="text-center space-y-1">
                          <Dna className="h-4 w-4 mx-auto text-blue-500" />
                          <p className="text-xl font-bold">{Math.round(t.protein)}g</p>
                          <p className="text-[10px] uppercase font-bold text-muted-foreground">Prot.</p>
                        </div>
                        <div className="text-center space-y-1">
                          <Wheat className="h-4 w-4 mx-auto text-yellow-600" />
                          <p className="text-xl font-bold">{Math.round(t.carbs)}g</p>
                          <p className="text-[10px] uppercase font-bold text-muted-foreground">Carbs</p>
                        </div>
                        <div className="text-center space-y-1">
                          <Droplets className="h-4 w-4 mx-auto text-red-500" />
                          <p className="text-xl font-bold">{Math.round(t.fat)}g</p>
                          <p className="text-[10px] uppercase font-bold text-muted-foreground">Fat</p>
                        </div>
                      </>
                    )
                  })()}
                </div>

                <div className="space-y-4">
                  <div className="flex items-center gap-2">
                    <Utensils className="h-5 w-5 text-primary" />
                    <h3 className="font-bold text-xl">Ingredients</h3>
                  </div>
                  <div className="grid gap-3">
                    {selectedRecipe.contents?.map((c, i) => (
                      <div key={i} className="flex justify-between items-center p-4 rounded-xl border border-border/60 bg-background/50 transition-colors hover:bg-muted/30">
                        <div className="space-y-0.5">
                          <p className="font-semibold text-sm">{c.ingredientName}</p>
                          <p className="text-xs text-muted-foreground font-medium">{c.ingredientBrand}</p>
                        </div>
                        <Badge variant="secondary" className="font-bold px-3 py-1 text-xs">
                          {c.ingredientAmount}g
                        </Badge>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>
    </div>
  )
}
