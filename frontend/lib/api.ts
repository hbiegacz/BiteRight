import { getToken, removeToken } from "./auth"

const API_BASE_URL = (process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080").replace(/\/$/, "") // TODO: move to .env

// Helper function for authenticated requests
export async function authFetch(endpoint: string, options: RequestInit = {}) {
  const token = getToken()
  const headers: HeadersInit = {
    "Content-Type": "application/json",
    ...options.headers,
  }

  if (token) {
    ; (headers as Record<string, string>)["Authorization"] = `Bearer ${token}`
  }

  const path = endpoint.startsWith("/") ? endpoint : `/${endpoint}`
  const response = await fetch(`${API_BASE_URL}${path}`, { ...options, headers })

  if (response.status === 401 || response.status === 403) {
    if (typeof window !== "undefined") {
      removeToken()
      window.location.href = "/login"
    }
  }

  return response
}

function formatDateForBackend(date: string): string {
  if (date.length === 10) {
    return `${date}T00:00:00`
  }
  return date
}

// Types
export interface DailyLimits {
  calorieLimit: number
  proteinLimit: number
  fatLimit: number
  carbLimit: number
  waterGoal: number
}

export interface DailySummary {
  totalCalories: number
  totalProtein: number
  totalFat: number
  totalCarbs: number
  totalWater: number
  caloriesBurnt: number
}

export interface MealContent {
  id: number
  ingredientId: number
  ingredientName: string
  ingredientAmount: number
}

export interface Meal {
  id: number
  name: string
  description: string
  mealDate: string
  mealTypeName: string
  mealTypeId: number
  contents: MealContent[]
}

export interface Ingredient {
  id: number
  name: string
  brand?: string
  portionSize: number
  calories: number
  protein: number
  fat: number
  carbs: number
}

export interface WaterIntake {
  id: number
  intakeDate: string
  waterAmount: number
}

export interface WeightHistory {
  id: number
  measurementDate: string
  weight: number
}

// Daily Limits API
export async function getDailyLimits(): Promise<DailyLimits | null> {
  try {
    const response = await authFetch("/dailyLimits/find")
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error getting daily limits:", e)
    return null
  }
}

export async function createDailyLimits(limits: DailyLimits): Promise<DailyLimits | null> {
  try {
    const response = await authFetch("/dailyLimits/create", {
      method: "POST",
      body: JSON.stringify(limits),
    })
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error creating daily limits:", e)
    return null
  }
}

export async function updateDailyLimits(limits: DailyLimits): Promise<DailyLimits | null> {
  try {
    const response = await authFetch("/dailyLimits/update", {
      method: "PUT",
      body: JSON.stringify(limits),
    })
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error updating daily limits:", e)
    return null
  }
}

// Daily Summary API
export async function getDailySummary(date: string): Promise<DailySummary | null> {
  try {
    const response = await authFetch(`/dailySummary/find?date=${date}`)
    if (response.ok) {
      const data = await response.json()
      if (!data) return null
      return {
        totalCalories: data.calories || 0,
        totalProtein: data.protein || 0,
        totalFat: data.fat || 0,
        totalCarbs: data.carbs || 0,
        totalWater: data.waterDrank || 0,
        caloriesBurnt: data.caloriesBurnt || 0,
      }
    }
    return null
  } catch (e) {
    console.error("Error getting daily summary:", e)
    return null
  }
}

export async function getSummaryRange(
  startDate: string,
  endDate: string
): Promise<DailySummary[] | null> {
  try {
    const response = await authFetch(
      `/dailySummary/find?startDate=${startDate}&endDate=${endDate}`
    )
    if (response.ok) {
      const summaries = await response.json()
      return (summaries || []).map((data: any) => ({
        totalCalories: data.calories || 0,
        totalProtein: data.protein || 0,
        totalFat: data.fat || 0,
        totalCarbs: data.carbs || 0,
        totalWater: data.waterDrank || 0,
        caloriesBurnt: data.caloriesBurnt || 0,
      }))
    }
    return null
  } catch (e) {
    console.error("Error getting summary range:", e)
    return null
  }
}

export async function getStreak(): Promise<number> {
  try {
    const response = await authFetch("/dailySummary/streak")
    if (response.ok) {
      return await response.json()
    }
    return 0
  } catch (e) {
    console.error("Error getting streak:", e)
    return 0
  }
}

export async function getAverageDailyCalories(startDate: string, endDate: string): Promise<number> {
  try {
    const response = await authFetch(`/dailySummary/averageCalories?startDate=${startDate}&endDate=${endDate}`)
    if (response.ok) {
      return await response.json()
    }
    return 0
  } catch (e) {
    console.error("Error getting average daily calories:", e)
    return 0
  }
}

export async function getAverageDailyProtein(startDate: string, endDate: string): Promise<number> {
  try {
    const response = await authFetch(`/dailySummary/averageProtein?startDate=${startDate}&endDate=${endDate}`)
    if (response.ok) {
      return await response.json()
    }
    return 0
  } catch (e) {
    console.error("Error getting average daily protein:", e)
    return 0
  }
}

export async function getUserWeight(): Promise<number | null> {
  try {
    const response = await authFetch("/userInfo/weight")
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error getting user weight:", e)
    return null
  }
}

// Meals API
export async function getMealsByDate(date: string): Promise<Meal[]> {
  try {
    const response = await authFetch(`/meal/findByDate/${date}`)
    if (response.ok) {
      return await response.json()
    }
    return []
  } catch (e) {
    console.error("Error getting meals by date:", e)
    return []
  }
}

export async function getAllUserMeals(): Promise<Meal[]> {
  try {
    const response = await authFetch("/meal/findUserMeals")
    if (response.ok) {
      return await response.json()
    }
    return []
  } catch (e) {
    console.error("Error getting all user meals:", e)
    return []
  }
}

export async function getMealById(id: number): Promise<Meal | null> {
  try {
    const response = await authFetch(`/meal/findByID/${id}`)
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error getting meal by id:", e)
    return null
  }
}

export async function createMeal(meal: {
  mealTypeId: number
  name: string
  description: string
  mealDate: string
  contents: { ingredientId: number; ingredientAmount: number }[]
}): Promise<Meal | null> {
  try {
    const response = await authFetch("/meal/create", {
      method: "POST",
      body: JSON.stringify({ ...meal, mealDate: formatDateForBackend(meal.mealDate) }),
    })
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error creating meal:", e)
    return null
  }
}

export async function updateMeal(
  id: number,
  meal: {
    mealTypeId: number
    name: string
    description: string
    mealDate: string
    contents: { ingredientId: number; ingredientAmount: number }[]
  }
): Promise<Meal | null> {
  try {
    const response = await authFetch(`/meal/update/${id}`, {
      method: "PUT",
      body: JSON.stringify({ ...meal, mealDate: formatDateForBackend(meal.mealDate) }),
    })
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error updating meal:", e)
    return null
  }
}

export async function deleteMeal(id: number): Promise<boolean> {
  try {
    const response = await authFetch(`/meal/delete/${id}`, { method: "DELETE" })
    return response.ok
  } catch (e) {
    console.error("Error deleting meal:", e)
    return false
  }
}

// Ingredients API
export async function searchIngredients(name: string): Promise<Ingredient[]> {
  try {
    const response = await authFetch(`/ingredient/find/${encodeURIComponent(name)}`)
    if (response.ok) {
      return await response.json()
    }
    return []
  } catch (e) {
    console.error("Error searching ingredients:", e)
    return []
  }
}

export async function createIngredient(ingredient: Omit<Ingredient, "id">): Promise<Ingredient | null> {
  try {
    const response = await authFetch("/ingredient/create", {
      method: "POST",
      body: JSON.stringify(ingredient),
    })
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error creating ingredient:", e)
    return null
  }
}

// Water Intake API
export async function getWaterIntakeByDate(date: string): Promise<WaterIntake[]> {
  try {
    const response = await authFetch(`/waterIntake/findWaterIntakesByDate/${date}`)
    if (response.ok) {
      const data = await response.json()
      return data.content || []
    }
    return []
  } catch {
    return []
  }
}

export async function getLastWaterIntake(): Promise<WaterIntake | null> {
  try {
    const response = await authFetch("/waterIntake/findLastWaterIntake")
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error getting last water intake:", e)
    return null
  }
}

export async function addWaterIntake(waterAmount: number, intakeDate: string): Promise<WaterIntake | null> {
  try {
    const response = await authFetch("/waterIntake/create", {
      method: "POST",
      body: JSON.stringify({ waterAmount, intakeDate: formatDateForBackend(intakeDate) }),
    })
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (error) {
    console.error("Error adding water intake:", error)
    return null
  }
}

// Weight History API
export async function getWeightHistoryByDate(date: string): Promise<WeightHistory[]> {
  try {
    const response = await authFetch(`/weightHistory/findWeightHistoriesByDate/${date}`)
    if (response.ok) {
      const data = await response.json()
      return data.content || []
    }
    return []
  } catch (e) {
    console.error("Error getting weight history by date:", e)
    return []
  }
}

export async function addWeight(weight: number, measurementDate: string): Promise<WeightHistory | null> {
  try {
    const response = await authFetch("/weightHistory/create", {
      method: "POST",
      body: JSON.stringify({ weight, measurementDate: formatDateForBackend(measurementDate) }),
    })
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error adding weight:", e)
    return null
  }
}

export async function getLastWeightHistory(): Promise<WeightHistory | null> {
  try {
    const response = await authFetch("/weightHistory/findLastWeightHistory")
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error getting last weight history:", e)
    return null
  }
}

export async function getWeightHistoryForUser(page = 0, size = 10): Promise<PagedResponse<WeightHistory> | null> {
  try {
    const response = await authFetch(`/weightHistory/findWeightHistoriesForUser?page=${page}&size=${size}`)
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error getting weight history for user:", e)
    return null
  }
}

// ==================== EXERCISE TYPES ====================

export interface ExerciseInfo {
  id: number
  name: string
  metabolicEquivalent: number
  caloriesPerMinute?: number
}

export interface UserExercise {
  id: number
  userId: number
  exerciseInfoId: number
  activityDate: string
  duration: number
  caloriesBurnt: number
  activityName: string
}

export interface PagedResponse<T> {
  content: T[]
  totalPages: number
  totalElements: number
  size: number
  number: number
}

// Exercise Info API (public exercise types)
export async function searchExerciseInfo(name: string): Promise<ExerciseInfo[]> {
  try {
    const response = await authFetch(`/exerciseInfo/find/${encodeURIComponent(name)}`)
    if (response.ok) {
      const data = await response.json()
      return (data || []).map((item: any) => ({
        id: item.id,
        name: item.name,
        metabolicEquivalent: item.metabolicEquivalent,
      }))
    }
    return []
  } catch (error) {
    console.error("Error searching exercise info:", error)
    return []
  }
}

export async function createExerciseInfo(exerciseInfo: ExerciseInfo): Promise<ExerciseInfo | null> {
  try {
    const response = await authFetch("/exerciseInfo/create", {
      method: "POST",
      body: JSON.stringify(exerciseInfo),
    })
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (error) {
    console.error("Error creating exercise info:", error)
    return null
  }
}

export async function updateExerciseInfo(name: string, exerciseInfo: ExerciseInfo): Promise<ExerciseInfo | null> {
  try {
    const response = await authFetch(`/exerciseInfo/update/${encodeURIComponent(name)}`, {
      method: "PUT",
      body: JSON.stringify(exerciseInfo),
    })
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (error) {
    console.error("Error updating exercise info:", error)
    return null
  }
}

export async function deleteExerciseInfo(name: string): Promise<boolean> {
  try {
    const response = await authFetch(`/exerciseInfo/delete/${encodeURIComponent(name)}`, {
      method: "DELETE",
    })
    return response.ok
  } catch (error) {
    console.error("Error deleting exercise info:", error)
    return false
  }
}

// User Exercise API (user's logged exercises)
export async function getUserExercises(page = 0, size = 10): Promise<PagedResponse<UserExercise> | null> {
  try {
    const response = await authFetch(`/userExercise/findExercisesForUser?page=${page}&size=${size}`)
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error getting user exercises:", e)
    return null
  }
}

export async function getExercisesByDate(date: string): Promise<PagedResponse<UserExercise> | null> {
  try {
    const response = await authFetch(`/userExercise/findExercisesByDate/${date}`)
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error getting exercises by date:", e)
    return null
  }
}

export async function getExerciseById(id: number): Promise<UserExercise | null> {
  try {
    const response = await authFetch(`/userExercise/findExerciseById/${id}`)
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error getting exercise by id:", e)
    return null
  }
}

export async function getLastExercise(): Promise<UserExercise | null> {
  try {
    const response = await authFetch("/userExercise/findLastExercise")
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error getting last exercise:", e)
    return null
  }
}

export async function createUserExercise(exercise: {
  exerciseInfoId: number
  activityDate: string
  duration: number
}): Promise<UserExercise | null> {
  try {
    const response = await authFetch("/userExercise/create", {
      method: "POST",
      body: JSON.stringify({ ...exercise, activityDate: formatDateForBackend(exercise.activityDate) }),
    })
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error creating user exercise:", e)
    return null
  }
}

export async function updateUserExercise(
  id: number,
  exercise: {
    exerciseInfoId: number
    activityDate: string
    duration: number
  }
): Promise<UserExercise | null> {
  try {
    const response = await authFetch(`/userExercise/update/${id}`, {
      method: "PUT",
      body: JSON.stringify({ ...exercise, activityDate: formatDateForBackend(exercise.activityDate) }),
    })
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error updating user exercise:", e)
    return null
  }
}

export async function deleteUserExercise(id: number): Promise<boolean> {
  try {
    const response = await authFetch(`/userExercise/delete/${id}`, { method: "DELETE" })
    return response.ok
  } catch (e) {
    console.error("Error deleting user exercise:", e)
    return false
  }
}

// ==================== RECIPE TYPES ====================

export interface RecipeMacrosDTO {
  calories: number
  protein: number
  fat: number
  carbs: number
}

export interface RecipeContentDTO {
  recipeContentId: number
  ingredientId: number
  ingredientName: string
  ingredientAmount: number
}

export interface RecipeDTO {
  recipeId: number
  name: string
  description: string
  imageUrl?: string
  contents: RecipeContentDTO[]
}

// Recipe API
export async function searchRecipes(name: string): Promise<RecipeDTO[]> {
  try {
    const response = await authFetch(`/recipe/findRecipes/${encodeURIComponent(name)}`)
    if (response.ok) {
      return await response.json()
    }
    return []
  } catch (error) {
    console.error("Error searching recipes:", error)
    return []
  }
}

export async function getRecipeByName(name: string): Promise<RecipeDTO | null> {
  try {
    const response = await authFetch(`/recipe/findByName/${encodeURIComponent(name)}`)
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (error) {
    console.error("Error getting recipe by name:", error)
    return null
  }
}

export async function getRecipeById(id: number): Promise<RecipeDTO | null> {
  try {
    const response = await authFetch(`/recipe/findById/${id}`)
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (error) {
    console.error("Error getting recipe by id:", error)
    return null
  }
}

export async function getRecipeMacros(id: number): Promise<RecipeMacrosDTO | null> {
  try {
    const response = await authFetch(`/recipe/getMacros/${id}`)
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (error) {
    console.error("Error getting recipe macros:", error)
    return null
  }
}

export async function createRecipe(recipe: {
  name: string
  description: string
  imageUrl?: string
  contents: { ingredientId: number; ingredientAmount: number }[]
}): Promise<RecipeDTO | null> {
  try {
    const response = await authFetch("/recipe/create", {
      method: "POST",
      body: JSON.stringify(recipe),
    })
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error creating recipe:", e)
    return null
  }
}

export async function updateRecipe(
  id: number,
  recipe: {
    name: string
    description: string
    imageUrl?: string
    contents: { ingredientId: number; ingredientAmount: number }[]
  }
): Promise<RecipeDTO | null> {
  try {
    const response = await authFetch(`/recipe/update/${id}`, {
      method: "PUT",
      body: JSON.stringify(recipe),
    })
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error updating recipe:", e)
    return null
  }
}

export async function deleteRecipe(id: number): Promise<boolean> {
  try {
    const response = await authFetch(`/recipe/delete/${id}`, { method: "DELETE" })
    return response.ok
  } catch (e) {
    console.error("Error deleting recipe:", e)
    return false
  }
}

// Recipe Content API (managing individual ingredients in a recipe)
export async function addRecipeContent(content: {
  recipeId: number
  ingredientId: number
  ingredientAmount: number
}): Promise<RecipeContentDTO | null> {
  try {
    const response = await authFetch("/recipeContent/add", {
      method: "POST",
      body: JSON.stringify(content),
    })
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error adding recipe content:", e)
    return null
  }
}

export async function updateRecipeContent(
  id: number,
  content: { ingredientAmount: number }
): Promise<RecipeContentDTO | null> {
  try {
    const response = await authFetch(`/recipeContent/update/${id}`, {
      method: "PUT",
      body: JSON.stringify(content),
    })
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error updating recipe content:", e)
    return null
  }
}

export async function deleteRecipeContent(id: number): Promise<boolean> {
  try {
    const response = await authFetch(`/recipeContent/delete/${id}`, { method: "DELETE" })
    return response.ok
  } catch (e) {
    console.error("Error deleting recipe content:", e)
    return false
  }
}

// ==================== USER & PROFILE TYPES ====================

export interface UserDTO {
  id: number
  username: string
  email: string
  type: string
  isVerified: boolean
}

export interface UserInfoDTO {
  id: number
  name: string
  surname: string
  age: number
  weight: number
  height: number
  lifestyle: string
  bmi: number
}

export interface UserGoalDTO {
  id: number
  goalType: string
  goalWeight: number
  deadline: string
}

export interface UserPreferencesDTO {
  id: number
  language: string
  darkmode: boolean
  font: string
  notifications: boolean
}

export interface Address {
  id?: number
  street: string
  city: string
  zipCode: string
  country: string
}

// User API
export async function getCurrentUser(): Promise<UserDTO | null> {
  try {
    const response = await authFetch("/user/find")
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error getting user:", e)
    return null
  }
}

// User Info (Profile) API
export async function getUserInfo(): Promise<UserInfoDTO | null> {
  try {
    const response = await authFetch("/userInfo/findUserInfo")
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error getting user info:", e)
    return null
  }
}

export async function updateUserInfo(userInfo: Omit<UserInfoDTO, "id">): Promise<UserInfoDTO | null> {
  try {
    const response = await authFetch("/userInfo/update", {
      method: "PUT",
      body: JSON.stringify(userInfo),
    })
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error updating user info:", e)
    return null
  }
}

export async function updateWeight(weight: number): Promise<boolean> {
  try {
    const response = await authFetch("/userInfo/updateWeight", {
      method: "PUT",
      body: JSON.stringify({ weight }),
    })
    return response.ok
  } catch (e) {
    console.error("Error updating weight:", e)
    return false
  }
}

// User Goal API
export async function getUserGoal(): Promise<UserGoalDTO | null> {
  try {
    const response = await authFetch("/userGoal/findUserGoal")
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error getting user goal:", e)
    return null
  }
}

export async function updateUserGoal(goal: {
  goalType: string
  goalWeight: number
  goalDate: string
}): Promise<UserGoalDTO | null> {
  try {
    const response = await authFetch("/userGoal/update", {
      method: "PUT",
      body: JSON.stringify(goal),
    })
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error updating user goal:", e)
    return null
  }
}

// User Preferences API
export async function getUserPreferences(): Promise<UserPreferencesDTO | null> {
  try {
    const response = await authFetch("/userPreferences/findUserPreferences")
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error getting user preferences:", e)
    return null
  }
}

export async function updateUserPreferences(preferences: Omit<UserPreferencesDTO, "id">): Promise<UserPreferencesDTO | null> {
  try {
    const response = await authFetch("/userPreferences/update", {
      method: "PUT",
      body: JSON.stringify(preferences),
    })
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error updating user preferences:", e)
    return null
  }
}

// Address API
export async function getUserAddresses(): Promise<Address[]> {
  try {
    const response = await authFetch("/address/find")
    if (response.ok) {
      return await response.json()
    }
    return []
  } catch (e) {
    console.error("Error getting user addresses:", e)
    return []
  }
}

export async function createAddress(address: Omit<Address, "id">): Promise<Address | null> {
  try {
    const response = await authFetch("/address/create", {
      method: "POST",
      body: JSON.stringify(address),
    })
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error creating address:", e)
    return null
  }
}

export async function updateAddress(id: number, address: Omit<Address, "id">): Promise<Address | null> {
  try {
    const response = await authFetch(`/address/update/${id}`, {
      method: "PUT",
      body: JSON.stringify(address),
    })
    if (response.ok) {
      return await response.json()
    }
    return null
  } catch (e) {
    console.error("Error updating address:", e)
    return null
  }
}

export async function deleteAddress(id: number): Promise<boolean> {
  try {
    const response = await authFetch(`/address/delete/${id}`, { method: "DELETE" })
    return response.ok
  } catch (e) {
    console.error("Error deleting address:", e)
    return false
  }
}
