import {z} from "zod";
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { Button } from "@/components/ui/button"
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
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
    <Form {...form}>
         {/* Form's submit function overriden by one above */}
        <form onSubmit={form.handleSubmit(formSubmit)}>
            <FormField 
                control={form.control}
                name="importcountry" 
                render={({field}) => (
                    <FormItem>
                        <FormLabel>Import Country</FormLabel>
                        <FormControl>
                            <Input placeholder="Singapore" {...field} />
                        </FormControl>
                        <FormDescription>
                        Please enter your importing country.
                        </FormDescription>
                        {/* Formmessage shows error messages */}
                        <FormMessage />
                    </FormItem>
                )
                }/>

                <FormField 
                control={form.control}
                name="exportcountry" 
                render={({field}) => (
                    <FormItem>
                        <FormLabel>Export Country</FormLabel>
                        <FormControl>
                            <Input placeholder="USA" {...field} />
                        </FormControl>
                        <FormDescription>
                        Please enter your exporting country.
                        </FormDescription>
                        {/* Formmessage shows error messages */}
                        <FormMessage />
                    </FormItem>
                )
                }/>

                <FormField 
                control={form.control}
                name="productcode" 
                render={({field}) => (
                    <FormItem>
                        <FormLabel>Product Code</FormLabel>
                        <FormControl>
                            <Input placeholder="HSXXX" {...field} />
                        </FormControl>
                        <FormDescription>
                        Please enter the product code of the product you wish to import.
                        </FormDescription>
                        {/* Formmessage shows error messages */}
                        <FormMessage />
                    </FormItem>
                )
                }/>

                <FormField 
                control={form.control}
                name="units" 
                render={({field}) => (
                    <FormItem>
                        <FormLabel>Quantity</FormLabel>
                        <FormControl>
                            <Input type="number" placeholder="1" {...field} />
                        </FormControl>
                        <FormDescription>
                        Please select the number of units you plan to import.
                        </FormDescription>
                        {/* Formmessage shows error messages */}
                        <FormMessage />
                    </FormItem>
                )
                }/>
                <Button type="submit">Calculate tariffs</Button>
        </form>
    </Form>
  )

}