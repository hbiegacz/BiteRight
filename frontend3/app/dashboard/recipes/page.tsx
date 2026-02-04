"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Search, BookOpen, Clock, Flame, Users } from "lucide-react"

// Placeholder recipes - in production this would come from an API
const sampleRecipes = [
  {
    id: 1,
    name: "Greek Yogurt Parfait",
    description: "Layered yogurt with fresh berries and granola",
    calories: 320,
    protein: 18,
    prepTime: 10,
    servings: 1,
    tags: ["Breakfast", "High Protein", "Quick"],
    image: "/placeholder.svg?height=200&width=300",
  },
  {
    id: 2,
    name: "Grilled Chicken Salad",
    description: "Fresh mixed greens with grilled chicken breast",
    calories: 450,
    protein: 42,
    prepTime: 25,
    servings: 2,
    tags: ["Lunch", "High Protein", "Low Carb"],
    image: "/placeholder.svg?height=200&width=300",
  },
  {
    id: 3,
    name: "Quinoa Buddha Bowl",
    description: "Nutritious bowl with quinoa, roasted veggies, and tahini",
    calories: 520,
    protein: 16,
    prepTime: 35,
    servings: 2,
    tags: ["Dinner", "Vegan", "High Fiber"],
    image: "/placeholder.svg?height=200&width=300",
  },
  {
    id: 4,
    name: "Protein Smoothie",
    description: "Banana, peanut butter, and whey protein blend",
    calories: 380,
    protein: 32,
    prepTime: 5,
    servings: 1,
    tags: ["Snack", "High Protein", "Quick"],
    image: "/placeholder.svg?height=200&width=300",
  },
  {
    id: 5,
    name: "Salmon with Vegetables",
    description: "Baked salmon fillet with roasted seasonal vegetables",
    calories: 480,
    protein: 38,
    prepTime: 40,
    servings: 2,
    tags: ["Dinner", "High Protein", "Omega-3"],
    image: "/placeholder.svg?height=200&width=300",
  },
  {
    id: 6,
    name: "Overnight Oats",
    description: "Creamy oats with chia seeds and fresh fruit",
    calories: 350,
    protein: 12,
    prepTime: 5,
    servings: 1,
    tags: ["Breakfast", "High Fiber", "Meal Prep"],
    image: "/placeholder.svg?height=200&width=300",
  },
]

const categories = ["All", "Breakfast", "Lunch", "Dinner", "Snack", "High Protein", "Vegan", "Quick"]

export default function RecipesPage() {
  const [searchQuery, setSearchQuery] = useState("")
  const [selectedCategory, setSelectedCategory] = useState("All")

  const filteredRecipes = sampleRecipes.filter((recipe) => {
    const matchesSearch =
      recipe.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      recipe.description.toLowerCase().includes(searchQuery.toLowerCase())
    const matchesCategory =
      selectedCategory === "All" || recipe.tags.includes(selectedCategory)
    return matchesSearch && matchesCategory
  })

  return (
    <div className="mx-auto max-w-6xl space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-foreground sm:text-3xl">Recipe Search</h1>
        <p className="mt-1 text-muted-foreground">
          Discover healthy recipes that fit your nutritional goals
        </p>
      </div>

      {/* Search and Filters */}
      <div className="space-y-4">
        <div className="flex gap-3">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input
              placeholder="Search recipes..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10"
            />
          </div>
        </div>

        {/* Category Filter */}
        <div className="flex flex-wrap gap-2">
          {categories.map((category) => (
            <Button
              key={category}
              variant={selectedCategory === category ? "default" : "outline"}
              size="sm"
              onClick={() => setSelectedCategory(category)}
            >
              {category}
            </Button>
          ))}
        </div>
      </div>

      {/* Recipe Grid */}
      {filteredRecipes.length === 0 ? (
        <div className="rounded-2xl border border-border bg-card p-12 text-center">
          <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-muted">
            <BookOpen className="h-8 w-8 text-muted-foreground" />
          </div>
          <h3 className="mt-4 text-lg font-semibold text-foreground">No recipes found</h3>
          <p className="mt-1 text-muted-foreground">Try adjusting your search or filters</p>
        </div>
      ) : (
        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {filteredRecipes.map((recipe) => (
            <div
              key={recipe.id}
              className="group overflow-hidden rounded-xl border border-border bg-card transition-shadow hover:shadow-lg"
            >
              <div className="aspect-video bg-muted">
                <div className="flex h-full items-center justify-center">
                  <BookOpen className="h-12 w-12 text-muted-foreground/50" />
                </div>
              </div>
              <div className="p-4">
                <h3 className="font-semibold text-foreground group-hover:text-primary transition-colors">
                  {recipe.name}
                </h3>
                <p className="mt-1 text-sm text-muted-foreground line-clamp-2">
                  {recipe.description}
                </p>

                <div className="mt-3 flex flex-wrap gap-1.5">
                  {recipe.tags.slice(0, 3).map((tag) => (
                    <Badge key={tag} variant="secondary" className="text-xs">
                      {tag}
                    </Badge>
                  ))}
                </div>

                <div className="mt-4 flex items-center gap-4 text-sm text-muted-foreground">
                  <div className="flex items-center gap-1">
                    <Flame className="h-4 w-4 text-accent" />
                    <span>{recipe.calories} kcal</span>
                  </div>
                  <div className="flex items-center gap-1">
                    <Clock className="h-4 w-4" />
                    <span>{recipe.prepTime} min</span>
                  </div>
                  <div className="flex items-center gap-1">
                    <Users className="h-4 w-4" />
                    <span>{recipe.servings}</span>
                  </div>
                </div>

                <Button className="mt-4 w-full bg-transparent" variant="outline">
                  View Recipe
                </Button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
