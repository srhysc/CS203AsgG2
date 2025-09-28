import {z} from "zod";
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { Button } from "@/components/ui/button"
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormMessage,
} from "@/components/ui/form"
import { Input } from "@/components/ui/input"

//declare formschema for form
export const tariffSchema = z.object({
    importcountry: z.string().min(1, "Import country is required"),
    exportcountry:z.string().min(1, "Export country is required"),
    productcode:z.string(),
    units: z.string().min(1,"must select at least 1 unit for calculation")
})


//TariffForm needs an onSubmit function, and will receive tariffSchema 
//Promise<void> promises to finish task - function can be sync or async, up to parent component
export function TariffForm({onSubmit} : {onSubmit: (data:z.infer<typeof tariffSchema>)=>void | Promise<void>}) {
  // 1. Define your form.
  const form = useForm<z.infer<typeof tariffSchema>>({
    resolver: zodResolver(tariffSchema),
    defaultValues: {
        importcountry: "",
        exportcountry:"",
        productcode:"",
        units: ""
    },
  })

  function formSubmit(values:z.infer<typeof tariffSchema>){
    //send filled tariffSchema up to parent
    onSubmit(values);
  }

  return(
    <div className="flex flex-col items-center">
     <h2 className="text-2xl font-bold mb-4">Calculate your Tariffs !</h2>

        <Form {...form}>
            {/* Form's submit function overriden by one above */}
            <form onSubmit={form.handleSubmit(formSubmit)} className="space-y-3">

                {/* Grid of fields */}
                <div className="grid grid-cols-2 gap-4 items-center">

                {/* Importing Country */}
                <label className="bg-white rounded-md p-3 flex items-center font-medium shadow">Importing Country:</label>
                <div className="bg-white rounded-md p-3 shadow">
                <FormField 
                    control={form.control}
                    name="importcountry" 
                    render={({field}) => (
                        <FormItem>
                            <FormControl>
                                <Input placeholder="Select/Type to Add" {...field} />
                            </FormControl>
                            {/* Formmessage shows error messages */}
                            <FormMessage className="!text-red-500 mt-1" />
                        </FormItem>
                    )
                    }/>
                </div>

                {/* Exporting Country */}
                <label className="bg-white rounded-md p-3 flex items-center font-medium shadow">Exporting Country:</label>
                <div className="bg-white rounded-md p-3 shadow">
                <FormField 
                    control={form.control}
                    name="exportcountry" 
                    render={({field}) => (
                        <FormItem>
                            <FormControl>
                                <Input placeholder="Select/Type to Add" {...field} />
                            </FormControl>
                            {/* Formmessage shows error messages */}
                            <FormMessage className="!text-red-500 mt-1"/>
                        </FormItem>
                )
                }/>
                </div>

                {/* Product Code */}
                <label className="bg-white rounded-md p-3 flex items-center font-medium shadow">Product Code:</label>
                <div className="bg-white rounded-md p-3 shadow">
                <FormField 
                    control={form.control}
                    name="productcode" 
                    render={({field}) => (
                        <FormItem>
                            <FormControl>
                                <Input placeholder="HSXXX" {...field} />
                            </FormControl>
                            {/* Formmessage shows error messages */}
                            <FormMessage className="!text-red-500 mt-1"/>
                        </FormItem>
                )
                }/>
                </div>

                {/* Quantity */}
                <label className="bg-white rounded-md p-3 flex items-center font-medium shadow">Quantity:</label>
                <div className="bg-white rounded-md p-3 shadow">
                <FormField 
                    control={form.control}
                    name="units" 
                    render={({field}) => (
                        <FormItem>
                            <FormControl>
                                <Input type="number" placeholder="1" {...field} />
                            </FormControl>
                            {/* Formmessage shows error messages */}
                            <FormMessage className="!text-red-500 mt-1"/>
                        </FormItem>
                )
                }/>
                </div>
            </div>
                {/* Submit button */}
                <Button
                    type="submit"
                    className="w-full bg-[#71869A] hover:bg-[#5a6a7c] text-white font-bold py-3 rounded-md shadow"
                >
                    CALCULATE
                </Button>
            </form>
        </Form>
    </div>
  )

}